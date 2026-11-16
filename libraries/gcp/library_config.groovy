fields {
    required {
        project_id = String
        region = String             // e.g. "europe-west1"
        credentials_id = String     // Jenkins secret-file credential: service account key JSON
    }
}
