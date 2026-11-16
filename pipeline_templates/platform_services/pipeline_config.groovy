/*
  Tier: Jenkins folder "platform". Multibranch jobs on the Website repo,
  one per service. The child tiers set service_dir and the libraries.
*/

@merge libraries {
    change_detection
    semgrep
    trivy {
        severity = 'HIGH,CRITICAL'
        fail_on_findings = true
    }
}

template_methods {
    install_dependencies
    lint
    unit_test
}
