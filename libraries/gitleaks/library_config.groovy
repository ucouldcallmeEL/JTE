/*
  Gitleaks: secret scanning of the repo's git history (gitleaks_scan).
  Rules: resources/gitleaks.toml (central). Accepted findings: the app repo's
  .gitleaksignore (finding fingerprints) or a "gitleaks:allow" comment on the line,
  owned by the app's team.
*/
fields {
    optional {
        fail_on_findings = Boolean  // default: true; false = report only, never fail
        full_history = Boolean      // default: false; true = scan every commit on every build
    }
}
