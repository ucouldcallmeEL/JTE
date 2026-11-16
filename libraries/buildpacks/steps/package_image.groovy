/*
  package_image: build the app image with Cloud Native Buildpacks and push it.
  Same step name as docker's package_image; a tier loads one or the other.

  Needs:  env.REGISTRY (artifact_registry_login), env.APP_NAME, env.GIT_COMMIT
  Sets:   env.IMAGE_REF = "<registry>/<app>:<short sha>", read by
          trivy_image_scan and helm_deploy
*/
void call() {
    // TODO: String imageRef = "${env.REGISTRY}/${env.APP_NAME}:${env.GIT_COMMIT.take(7)}"
    //       sh "pack build ${imageRef} --builder ${config.builder ?: 'paketobuildpacks/builder-jammy-base'} --publish"
    //       env.IMAGE_REF = imageRef
}
