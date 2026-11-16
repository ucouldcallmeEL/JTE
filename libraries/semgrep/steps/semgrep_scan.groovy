/*
  semgrep_scan: static analysis of the source.
  Writes reports/semgrep.json (picked up by devflow_api's report_scan_results).
*/
void call() {
    // TODO: sh "semgrep scan --config ${config.rules ?: 'p/default'} --json --output reports/semgrep.json"
    //       add --error when findings should fail the build
}
