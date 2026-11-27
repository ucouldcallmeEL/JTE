/*
  trivy_common_flags: internal helper for trivy_fs_scan and trivy_image_scan;
  templates don't call it.

  Writes the central policy (resources/trivy.yaml) to this build's tmp dir,
  not the workspace (a Dockerfile's COPY . . would put it in the image),
  and returns the flags every Trivy run shares:
    --config     the central policy, so the app repo can't override it
    --ignorefile the app's .trivyignore.yaml when it has one (supports a
                 statement and expired_at per entry). Without it, Trivy picks
                 up a plain .trivyignore by itself.
*/
String call() {
    String policy = "${pwd(tmp: true)}/devflow-trivy.yaml"
    writeFile file: policy, text: resource('trivy.yaml')
    String flags = "--config \"${policy}\""
    if (fileExists('.trivyignore.yaml')) {
        flags += ' --ignorefile .trivyignore.yaml'
    }
    return flags
}
