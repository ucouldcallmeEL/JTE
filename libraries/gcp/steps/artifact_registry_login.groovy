/*
  artifact_registry_login: let docker/pack push to an Artifact Registry repo.

      artifact_registry_login('user-apps')

  Sets: env.REGISTRY = "<region>-docker.pkg.dev/<project_id>/<repo>"
*/
void call(String repo) {
    // TODO: sh "gcloud auth configure-docker ${config.region}-docker.pkg.dev --quiet"
    //       env.REGISTRY = "${config.region}-docker.pkg.dev/${config.project_id}/${repo}"
}
