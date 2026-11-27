/*
  gitleaks_scan: look for secrets (API keys, tokens, passwords, private keys)
  in the repo's git history. Writes reports/gitleaks.json, secrets redacted,
  picked up by devflow_api's report_scan_results.

  Full scan the first time, then only the commits added since:
    - When a scan passes, the build records env.GITLEAKS_SCANNED_COMMIT and
      env.GITLEAKS_RULES_HASH (hash of the central gitleaks.toml).
    - The next build looks back for the most recent build with that record and
      scans <that commit>..HEAD.
    - Full history instead when: there is no record (first build, or builds from
      before Gitleaks), the central rules changed, the recorded commit is gone
      (force-push / rebase), or full_history = true.
  A scan that finds secrets records nothing, so the next build scans the same
  commits again: the finding stays until the secret is rotated and its
  fingerprint added to .gitleaksignore.

  Needs: a full clone (git_checkout does one) and the gitleaks binary.
*/
void call() {
    String head = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()

    String rules = "${pwd(tmp: true)}/devflow-gitleaks.toml"
    writeFile file: rules, text: resource('gitleaks.toml')
    String rulesHash = sh(script: "sha256sum \"${rules}\" | cut -d' ' -f1", returnStdout: true).trim()

    Map plan = scan_plan(head, rulesHash)
    echo "Gitleaks: ${plan.description}"

    sh 'mkdir -p reports'
    if (plan.range && plan.commits == 0) {
        writeFile file: 'reports/gitleaks.json', text: '[]\n'
        record_scan(head, rulesHash)
        return
    }

    // --exit-code 2 for leaks, so it's not confused with gitleaks itself failing (1).
    String logOpts = plan.range ? "--log-opts=\"${plan.range}\"" : ''
    int status = sh(returnStatus: true, script: """
        gitleaks git . \
            --config "${rules}" \
            --redact \
            --no-banner \
            --report-format json \
            --report-path reports/gitleaks.json \
            --exit-code 2 \
            ${logOpts}
    """)

    if (status == 0) {
        record_scan(head, rulesHash)
    } else if (status == 2) {
        String message = 'Gitleaks found secrets (details redacted in reports/gitleaks.json). ' +
                         'Rotate each leaked secret first: removing it from the code does not remove it ' +
                         'from git history. Then add its Fingerprint to .gitleaksignore.'
        if (config.fail_on_findings == false) {
            echo "WARNING: ${message} Not failing: fail_on_findings = false."
            record_scan(head, rulesHash)
        } else {
            error message
        }
    } else {
        error "gitleaks failed to run (exit status ${status}), see the log above."
    }
}

// What to scan: [range: null] = full history, or [range: 'abc..HEAD', commits: n].
Map scan_plan(String head, String rulesHash) {
    if (config.full_history) {
        return [description: 'full history scan (full_history = true).']
    }
    Map previous = previous_scan()
    if (!previous) {
        return [description: 'full history scan (no earlier Gitleaks scan for this job).']
    }
    if (previous.rulesHash != rulesHash) {
        return [description: 'full history scan (the central rules changed since the last scan).']
    }
    String last = previous.commit
    if (!(last ==~ /[0-9a-f]{40}/) ||
            sh(script: "git cat-file -e ${last}^{commit}", returnStatus: true) != 0) {
        return [description: "full history scan (last scanned commit ${last.take(7)} is no longer in the repo, force-push or rebase?)."]
    }
    String range = "${last}..${head}"
    int commits = sh(script: "git rev-list --count ${range}", returnStdout: true).trim() as int
    String what = commits == 0 ? 'nothing new to scan' : "${commits} new commit(s)"
    return [range: range, commits: commits,
            description: "incremental scan ${last.take(7)}..${head.take(7)}: ${what}."]
}

// The most recent earlier build of this job where gitleaks_scan passed.
Map previous_scan() {
    def build = currentBuild.previousBuild
    for (int i = 0; build != null && i < 50; i++) {
        Map vars = build.buildVariables
        if (vars.GITLEAKS_SCANNED_COMMIT) {
            return [commit: vars.GITLEAKS_SCANNED_COMMIT, rulesHash: vars.GITLEAKS_RULES_HASH]
        }
        build = build.previousBuild
    }
    return null
}

// Saved with this build; the next build's previous_scan() reads it.
void record_scan(String commit, String rulesHash) {
    env.GITLEAKS_SCANNED_COMMIT = commit
    env.GITLEAKS_RULES_HASH = rulesHash
}
