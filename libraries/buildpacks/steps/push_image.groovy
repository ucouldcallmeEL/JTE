/*
  push_image: push env.IMAGE_REF (set by build_image) to the registry.
  Call it inside the same registry_login block, after trivy_image_scan,
  so only images that passed the gate get pushed.
*/
void call() {
    if (!env.IMAGE_REF) {
        error 'push_image: env.IMAGE_REF is not set, run build_image first.'
    }
    sh "docker push ${env.IMAGE_REF}"
    echo "Pushed ${env.IMAGE_REF}"
}
