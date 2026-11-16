/*
  package_image: docker build + push from the repo's own Dockerfile.
  Used by the platform services and by the "Custom OCI" user-app option.
  Same step name as buildpacks' package_image; a tier loads one or the other.

  Needs:  env.REGISTRY (artifact_registry_login), env.GIT_COMMIT
  Sets:   env.IMAGE_REF = "<registry>/<image>:<short sha>", read by
          trivy_image_scan and helm_deploy
*/
void call() {
    // TODO: String name = config.image_name ?: env.APP_NAME
    //       String imageRef = "${env.REGISTRY}/${name}:${env.GIT_COMMIT.take(7)}"
    //       sh "docker build -f ${config.dockerfile ?: 'Dockerfile'} -t ${imageRef} ."
    //       sh "docker push ${imageRef}"
    //       env.IMAGE_REF = imageRef
}
