/*
  Semgrep: static analysis (semgrep_scan) with rules kept in this repo,
  resources/rules/<name>.yml. Add a ruleset with
  scripts/update-semgrep-rules.sh <name>, then list it in rulesets.
*/
fields {
    optional {
        rulesets = List             // default: ["default"] (Semgrep Registry's p/default)
        fail_on_findings = Boolean  // default: true; false = report only. Only ERROR findings fail.
    }
}
