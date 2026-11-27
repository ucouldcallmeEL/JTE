/*
  trivy_image_scan: scan the image build_image just built (env.IMAGE_REF),
  from the local Docker daemon, before push_image pushes it.
  Scans OS and app packages for CVEs, and image layers for secrets.
  No license scan here: base images ship GPL-3.0 OS packages (bash, coreutils...)
  that would fail every build; trivy_fs_scan checks the app's own dependencies.

  Two runs, like trivy_fs_scan:
    1. report: reports/trivy-image.json for DevFlow, unfixed findings included.
       Also converted to a CycloneDX SBOM, reports/sbom-<short sha>.json,
       archived as a build artifact.
    2. gate: --ignore-unfixed, table output; fails on fixable findings unless
       fail_on_findings = false.
*/
void call() {
    if (!env.IMAGE_REF) {
        error 'trivy_image_scan: env.IMAGE_REF is not set, run build_image first'
    }
    String severity = config.severity ?: 'HIGH,CRITICAL'
    int exitCode = config.fail_on_findings == false ? 0 : 1
    String common = "${env.IMAGE_REF} ${trivy_common_flags()} --image-src docker --scanners vuln,secret --severity ${severity}"
    String sbom = "reports/sbom-${env.GIT_COMMIT.take(7)}.json"

    sh 'mkdir -p reports'
    // --list-all-pkgs keeps every package in the JSON so the SBOM is complete
    sh "trivy image ${common} --exit-code 0 --list-all-pkgs --format json --output reports/trivy-image.json"
    sh "trivy convert --format cyclonedx --output ${sbom} reports/trivy-image.json"
    archiveArtifacts artifacts: sbom, fingerprint: true
    sh "trivy image ${common} --ignore-unfixed --exit-code ${exitCode}"
}
