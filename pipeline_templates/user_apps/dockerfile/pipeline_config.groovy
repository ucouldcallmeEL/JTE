/*
  Tier: Jenkins folder "user-apps/dockerfile" ("Custom OCI" in the UI).
  The repo's Dockerfile does its own build and tests, so no language library:
  install_dependencies/lint/unit_test stay no-ops.
*/
libraries {
    docker
}
