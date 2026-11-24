/*
  registry_login (Docker Hub): log docker in for the steps inside the block.
  The login lives in a docker config dir of this build's own, deleted
  afterwards, so the token doesn't stay on the agent.

      registry_login { package_image() }

  Sets: env.IMAGE_REPOSITORY = "docker.io/<repository>"  if config.repository
        env.REGISTRY         = "docker.io/<namespace>"   if config.namespace
*/
void call(Closure body) {
    if (config.repository) {
        env.IMAGE_REPOSITORY = "docker.io/${config.repository}"
    } else if (config.namespace) {
        env.REGISTRY = "docker.io/${config.namespace}"
    } else {
        error 'dockerhub: set repository or namespace in the pipeline config.'
    }

    String dockerConfig = "${pwd(tmp: true)}/docker-config"
    withEnv(["DOCKER_CONFIG=${dockerConfig}"]) {
        try {
            withCredentials([usernamePassword(credentialsId: config.credentials_id,
                                              usernameVariable: 'DOCKERHUB_USER',
                                              passwordVariable: 'DOCKERHUB_TOKEN')]) {
                sh 'echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USER" --password-stdin docker.io'
            }
            body()
        } finally {
            sh "rm -rf \"${dockerConfig}\""
        }
    }
}
