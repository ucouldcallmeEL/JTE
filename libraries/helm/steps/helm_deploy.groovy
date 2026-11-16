/*
  helm_deploy: helm upgrade --install the release with env.IMAGE_REF.
  --atomic rolls back on its own if the release doesn't become ready.

  Needs:  env.IMAGE_REF; for user apps also env.APP_NAME, env.DEPLOY_ENV,
          env.DEVFLOW_MODULES (e.g. "postgres,redis" -> chart values)
  Sets:   env.APP_URL = "https://<release>.<domain>" (read by report_deployment)
*/
void call() {
    // TODO: helm upgrade --install <release> ${config.chart} -n <namespace> --create-namespace \
    //         --set image=${env.IMAGE_REF} --set ingress.host=<release>.${config.domain} \
    //         --atomic --wait --timeout ${config.timeout ?: '5m'}
    //       env.APP_URL = "https://<release>.${config.domain}"
}
