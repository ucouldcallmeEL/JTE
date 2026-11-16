// Tier: Jenkins folder "platform/backend". FastAPI service in Website/backend.
keywords {
    service_dir = 'backend'
}

libraries {
    python
    docker {
        image_name = 'devflow-backend'
    }
    helm {
        chart = 'oci://europe-west1-docker.pkg.dev/TODO-gcp-project/charts/devflow-backend'
        domain = 'devflow.example'      // TODO
        release_name = 'devflow-backend'
        namespace = 'devflow'
    }
}
