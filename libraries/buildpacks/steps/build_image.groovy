/*
  build_image: build the app image with Cloud Native Buildpacks into the local
  daemon only (no --publish); push_image pushes it after trivy_image_scan.
  Same step name as docker's build_image; a tier loads one or the other.

  Needs:  env.REGISTRY (artifact_registry_login), env.APP_NAME, env.GIT_COMMIT
  Sets:   env.IMAGE_REF = "<registry>/<app>:<short sha>", read by
          trivy_image_scan, push_image and helm_deploy
*/
void call() {
    // TODO: String imageRef = "${env.REGISTRY}/${env.APP_NAME}:${env.GIT_COMMIT.take(7)}"
    //       sh "pack build ${imageRef} --builder ${config.builder ?: 'paketobuildpacks/builder-jammy-base'}"   // no --publish: push_image pushes
    //       env.IMAGE_REF = imageRef
}
