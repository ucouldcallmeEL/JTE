/*
  Tier: Jenkins folder "user-apps". Shared by the language tiers below it.

  The DevFlow backend creates one job per project in user-apps/<framework>/,
  with the project's repo as the job's SCM and these parameters:

    APP_NAME            DNS-safe name: image name, Helm release, hostname
    DEPLOY_ENV          production | staging | preview
    DEVFLOW_PROJECT_ID  projects.id
    DEVFLOW_RUN_ID      pipeline_runs.id that stage/scan reports belong to
    DEVFLOW_MODULES     comma-separated add-ons, e.g. "postgres,redis"
*/

@merge libraries {
    git
    devflow_api {
        api_url = 'https://api.devflow.example'     // TODO
        token_credentials_id = 'devflow-api-token'
    }
    semgrep
    trivy {
        severity = 'HIGH,CRITICAL'
        fail_on_findings = true
    }
    helm {
        chart = 'oci://europe-west1-docker.pkg.dev/TODO-gcp-project/charts/devflow-app'
        domain = 'apps.devflow.example'             // TODO
    }
}

// Language libraries implement these; any a library leaves out is a no-op.
template_methods {
    install_dependencies
    lint
    unit_test
}
