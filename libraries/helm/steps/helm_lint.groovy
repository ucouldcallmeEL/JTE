/*
  helm_lint: render the chart with this release's values and fail on errors,
  before anything is deployed.
*/
void call() {
    // TODO: sh "helm lint ${config.chart} ..." (helm template | kubeconform works for OCI charts too)
}
