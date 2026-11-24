// Temporary registry until the GCP account exists (then gcp's Artifact Registry).
// Set repository OR namespace.
fields {
    required {
        credentials_id = String     // Jenkins username/password credential: Docker Hub user + access token
    }
    optional {
        repository = String         // "<user>/<repo>": every app in this one repo, tagged <app>-<sha>
                                    // (Docker Hub's free plan has a single private repo)
        namespace = String          // "<user>": one repo per app, <user>/<app>:<sha> (public on the free plan)
    }
}
