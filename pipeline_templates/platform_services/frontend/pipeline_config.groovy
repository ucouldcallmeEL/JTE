/*
  Tier: Jenkins folder "platform/frontend". React/Vite app in Website/frontend.
  TODO: the frontend has no Dockerfile yet (vite build + a static file server).
*/
keywords {
    service_dir = 'frontend'
}

libraries {
    npm
    docker {
        image_name = 'devflow-frontend'
    }
    helm {
        chart = 'oci://europe-west1-docker.pkg.dev/TODO-gcp-project/charts/devflow-frontend'
        domain = 'devflow.example'      // TODO
        release_name = 'devflow-frontend'
        namespace = 'devflow'
    }
}
