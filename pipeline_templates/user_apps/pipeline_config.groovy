/*
  Tier: Jenkins folder "user-apps". Shared by the language tiers below it.

  The DevFlow backend creates one job per project, user-apps/<pipeline>/<APP_NAME>,
  and passes these parameters on every build. The list is the contract with
  the backend (Website/backend/app/services/jenkins_jobs.py); change both together.

    GIT_REPO_URL        repo to check out (git_checkout)
    GIT_BRANCH          branch to build
    GIT_CREDENTIALS_ID  Jenkins credential for a private repo; empty for public ones
    APP_NAME            the app name, unique across DevFlow, a DNS label:
                        image tag now; Helm release and hostname later
    DEVFLOW_PROJECT_ID  projects.id, for reports back to DevFlow
    START_COMMAND       optional: how the container starts the app, e.g.
                        "uvicorn main:app --host 0.0.0.0 --port $PORT"
                        (default: the repo's Procfile web: line, main.py, app.py)
    PORT                optional: port the app listens on (default 8000)

  Planned, once deploys and stored runs exist:
    DEPLOY_ENV          production | staging | preview
    DEVFLOW_RUN_ID      pipeline_runs.id that stage/scan reports belong to
    DEVFLOW_MODULES     comma-separated add-ons, e.g. "postgres,redis"
*/

@merge libraries {
    git
    devflow_api {
        api_url = 'https://api.devflow.example'     // TODO
        token_credentials_id = 'devflow-api-token'
    }
    dockerhub {                                     // temporary, until GCP's Artifact Registry
        credentials_id = 'dockerhub-credentials'
        repository = 'malakwagdy/devflow'
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
    prepare_dockerfile      // default Dockerfile for repos without one
}
