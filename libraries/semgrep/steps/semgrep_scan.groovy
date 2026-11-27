/*
  semgrep_scan: static analysis of the source with the rules kept in this
  repo (resources/rules/<ruleset>.yml, refreshed by scripts/update-semgrep-rules.sh).
  Builds never download rules and send no metrics.

  One run writes reports/semgrep.json (picked up by devflow_api's
  report_scan_results) and prints the findings in the console. Then gate.py:
    ERROR findings      fail the build (unless fail_on_findings = false)
    WARNING / INFO      reported only

  Exceptions are the app repo's own: "# nosemgrep: <rule id>" on a line, or
  paths in a .semgrepignore file.
*/
void call() {
    List<String> rulesets = config.rulesets ?: ['default']
    String tmp = pwd(tmp: true)
    String rulesDir = "${tmp}/semgrep-rules"
    sh "rm -rf \"${rulesDir}\""
    for (String name : rulesets) {
        writeFile file: "${rulesDir}/${name}.yml", text: resource("rules/${name}.yml")
    }
    writeFile file: "${tmp}/semgrep-gate.py", text: resource('gate.py')

    sh 'mkdir -p reports'
    // Exit status 0 = scan ran (findings are judged by gate.py), anything else = semgrep failed.
    int status = sh(returnStatus: true, script: """
        semgrep scan \
            --config "${rulesDir}" \
            --metrics=off \
            --disable-version-check \
            --exclude .venv --exclude node_modules --exclude reports \
            --text \
            --json-output=reports/semgrep.json
    """)
    if (status != 0) {
        error "semgrep failed to run (exit status ${status}), see the log above."
    }

    String fail = config.fail_on_findings == false ? 'no' : 'yes'
    int gate = sh(returnStatus: true, script: "python3 \"${tmp}/semgrep-gate.py\" reports/semgrep.json ${fail}")
    if (gate == 3) {
        error 'Semgrep found ERROR-level issues, listed above.'
    } else if (gate != 0) {
        error "Semgrep gate failed to run (exit status ${gate})."
    }
}
