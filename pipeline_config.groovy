/*
  Global governance tier: applies to every DevFlow job.
  Lower tiers may add libraries (@merge) but can't change anything set here.
*/

jte {
    allow_scm_jenkinsfile = false       // app repos can't bring their own Jenkinsfile
    permissive_initialization = false   // two loaded libraries can't define the same step
}

@merge libraries {
    gcp {
        project_id = 'TODO-gcp-project'
        region = 'europe-west1'
        credentials_id = 'gcp-jenkins-sa'
    }
}
