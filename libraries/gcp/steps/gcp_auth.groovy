/*
  gcp_auth: activate the Jenkins service account so the gcloud, docker, helm
  and terraform calls after it are authenticated.

  Terraform reads GOOGLE_APPLICATION_CREDENTIALS, not gcloud's login, so the
  key file has to stay reachable for terraform_* steps too.
*/
void call() {
    // TODO: withCredentials([file(credentialsId: config.credentials_id, variable: 'GCP_KEY')]) {
    //           sh 'gcloud auth activate-service-account --key-file "$GCP_KEY"'
    //       }
    //       sh "gcloud config set project ${config.project_id}"
}
