fields {
    optional {
        image_name = String     // default: env.APP_NAME; platform tiers set it
        dockerfile = String     // default: "Dockerfile"; without one, the language library's prepare_dockerfile
        platform = String       // default: "linux/amd64" (the GCP VMs), also when Jenkins runs on arm64
    }
}
