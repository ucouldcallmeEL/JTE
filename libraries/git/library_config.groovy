/*
  Fallbacks for jobs that always build the same repo. User-app jobs leave
  these out and get the repo per run from job parameters instead.
*/
fields {
    optional {
        repo_url = String           // default: params.GIT_REPO_URL
        branch = String             // default: params.GIT_BRANCH, then "main"
        credentials_id = String     // default: params.GIT_CREDENTIALS_ID
    }
}
