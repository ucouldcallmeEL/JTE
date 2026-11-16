fields {
    required {
        chart = String          // e.g. "oci://europe-west1-docker.pkg.dev/<project>/charts/devflow-app"
        domain = String         // the app is served at https://<release>.<domain>
    }
    optional {
        release_name = String               // default: env.APP_NAME
        namespace = String                  // default: one namespace per project (env.DEVFLOW_PROJECT_ID)
        values_file = String
        kubeconfig_credentials_id = String  // omit when Jenkins runs inside the cluster
        timeout = String                    // default: "5m"
    }
}
