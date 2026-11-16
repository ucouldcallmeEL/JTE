/*
  trivy_fs_scan: scan the checked-out source for vulnerable dependencies,
  leaked secrets and misconfigurations (Dockerfiles, manifests).
  Writes reports/trivy-fs.json (picked up by devflow_api's report_scan_results).
*/
void call() {
    String severity = config.severity ?: 'HIGH,CRITICAL'
    int exitCode = config.fail_on_findings == false ? 0 : 1

    sh 'mkdir -p reports'
    sh """
        trivy fs . \
            --scanners vuln,secret,misconfig \
            --severity ${severity} \
            --exit-code ${exitCode} \
            --format json \
            --output reports/trivy-fs.json
    """
}
