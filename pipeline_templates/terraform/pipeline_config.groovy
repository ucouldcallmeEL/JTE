// Tier: Jenkins folder "infrastructure/terraform". Multibranch job on the infra repo.
libraries {
    terraform {
        working_dir = 'environments/prod'   // TODO
    }
}
