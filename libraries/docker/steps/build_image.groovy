/*
  build_image: docker build into the local daemon only. Nothing is pushed:
  trivy_image_scan checks the image first, then push_image pushes it.

  Dockerfile: the repo's own (config.dockerfile, default "Dockerfile") if it
  has one, otherwise the language library's default from prepare_dockerfile
  (e.g. python's resources/Dockerfile). Used by the platform services, the
  "Custom OCI" user-app tier and the language tiers.
  Same step name as buildpacks' build_image; a tier loads one or the other.

  Images are built for config.platform (default linux/amd64, what the GCP
  VMs run), even on an arm64 Jenkins host such as an Apple Silicon Mac.

  Needs:  a registry login around it, e.g.
            registry_login { build_image(); trivy_image_scan(); push_image() },
          which sets env.REGISTRY (one repo per image) or env.IMAGE_REPOSITORY
          (every app in one repo, app name in the tag); env.GIT_COMMIT
  Sets:   env.IMAGE_REF, read by trivy_image_scan, push_image and the deploy step:
            "<registry>/<image>:<short sha>"  or  "<repository>:<app>-<short sha>"
*/
void call() {
    String imageRef = image_ref()
    Map build = dockerfile_to_use()
    ignore_pipeline_files()

    String platform = config.platform ?: 'linux/amd64'
    String buildArgs = build.args.keySet().collect { "--build-arg ${it}" }.join(' ')
    // Values go through the environment (--build-arg NAME), not the command
    // line, so a START_COMMAND with quotes or $ can't break the shell.
    withEnv(build.args.collect { k, v -> "${k}=${v}" }) {
        sh "docker build --platform ${platform} -f \"${build.file}\" ${buildArgs} -t ${imageRef} ."
    }
    env.IMAGE_REF = imageRef
    echo "Built ${imageRef} (not pushed yet)"
}

String image_ref() {
    String tag = env.GIT_COMMIT.take(7)
    if (env.IMAGE_REPOSITORY) {
        if (!env.APP_NAME) {
            error 'build_image: APP_NAME job parameter is missing.'
        }
        return "${env.IMAGE_REPOSITORY}:${env.APP_NAME}-${tag}"
    }
    if (!env.REGISTRY) {
        error 'build_image: no registry. Call it inside registry_login { build_image() }.'
    }
    String name = config.image_name ?: env.APP_NAME
    if (!name) {
        error 'build_image: set image_name in the docker library config or the APP_NAME job parameter.'
    }
    return "${env.REGISTRY}/${name}:${tag}"
}

Map dockerfile_to_use() {
    String own = config.dockerfile ?: 'Dockerfile'
    if (fileExists(own)) {
        echo "Building with the repo's ${own}."
        return [file: own, args: [:]]
    }
    Map generated = prepare_dockerfile()    // template method: the language library's default
    if (!generated) {
        error "The repo has no ${own} and this pipeline's language has no default Dockerfile."
    }
    echo "The repo has no ${own}; building with the pipeline's default Dockerfile."
    return generated
}

// Keep what earlier stages left in the workspace (.venv, caches, reports)
// out of the build context, so it never lands in the image.
void ignore_pipeline_files() {
    List<String> entries = ['.venv', 'reports', '.ruff_cache', '.pytest_cache', '**/__pycache__']
    String existing = fileExists('.dockerignore') ? readFile('.dockerignore') : ''
    List<String> missing = entries.findAll { !existing.readLines().contains(it) }
    if (missing) {
        String sep = (existing && !existing.endsWith('\n')) ? '\n' : ''
        writeFile file: '.dockerignore', text: existing + sep + missing.join('\n') + '\n'
    }
}
