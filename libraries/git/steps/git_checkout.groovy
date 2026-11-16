/*
  git_checkout: clone the repo this run builds.

  Repo, branch and credential come from job parameters (GIT_REPO_URL,
  GIT_BRANCH, GIT_CREDENTIALS_ID), falling back to this library's config.
  With no repo URL at all, falls back to the job's own SCM (checkout scm).

  Returns: checkout's map, so the template can read .GIT_COMMIT
*/
Map call() {
    String repoUrl = params.GIT_REPO_URL ?: config.repo_url
    String branch = params.GIT_BRANCH ?: config.branch ?: 'main'
    String credentialsId = params.GIT_CREDENTIALS_ID ?: config.credentials_id

    if (!repoUrl) {
        echo 'No repo URL given, checking out the job\'s own SCM.'
        return checkout(scm)
    }

    echo "Checking out ${repoUrl} (branch ${branch})"
    return checkout([
        $class: 'GitSCM',
        branches: [[name: "*/${branch}"]],
        userRemoteConfigs: [[url: repoUrl, credentialsId: credentialsId]]
    ])
}

