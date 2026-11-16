/*
  gcp_secret: read one secret from Secret Manager, e.g. an app's env vars at
  deploy time. Never echo the value.

      String dbUrl = gcp_secret('project-42-database-url')
*/
String call(String name) {
    // TODO: sh(returnStdout: true, script: "gcloud secrets versions access latest --secret=${name} --project=${config.project_id}").trim()
}
