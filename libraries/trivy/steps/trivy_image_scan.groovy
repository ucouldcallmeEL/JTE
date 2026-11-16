/*
  trivy_image_scan: scan the image package_image just pushed (env.IMAGE_REF).
  Writes reports/trivy-image.json. Same options as trivy_fs_scan.
*/
void call() {
    // TODO: trivy image ${env.IMAGE_REF} --severity ... --exit-code ... --format json --output reports/trivy-image.json
}
