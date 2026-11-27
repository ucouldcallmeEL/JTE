/*
  trivy_fs_scan: scan the checked-out source (run after install_dependencies)
  for vulnerable dependencies, misconfigurations (Dockerfiles, IaC) and
  dependency licenses against the central policy. Secrets are Gitleaks' job.

  Two runs:
    1. report: every finding at config.severity, fixable or not, written to
       reports/trivy-fs.json for DevFlow (devflow_api's report_scan_results).
       Never fails the build.
    2. gate: same scan with --ignore-unfixed, table output in the console log.
       Fails the build on findings that have a fix (and on forbidden licenses),
       unless fail_on_findings = false.
*/
void call() {
    String severity = config.severity ?: 'HIGH,CRITICAL'
    int exitCode = config.fail_on_findings == false ? 0 : 1
    String common = "${trivy_common_flags()} --scanners vuln,misconfig,license --severity ${severity}"

    sh 'mkdir -p reports'
    sh "trivy fs . ${common} --exit-code 0 --format json --output reports/trivy-fs.json"
    sh "trivy fs . ${common} --ignore-unfixed --exit-code ${exitCode}"
}
