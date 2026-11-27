/*
  Trivy: trivy_fs_scan (source) and trivy_image_scan (built image, before push).
  License policy: resources/trivy.yaml (central). Accepted risks: the app
  repo's .trivyignore or .trivyignore.yaml, owned by the app's team.
*/
fields {
    optional {
        severity = String           // default: "HIGH,CRITICAL"
        fail_on_findings = Boolean  // default: true; false = report only, never fail
    }
}
