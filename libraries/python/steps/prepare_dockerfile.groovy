/*
  prepare_dockerfile (Python): the default Dockerfile for Python apps that
  don't have their own (resources/Dockerfile), written to the workspace's
  tmp dir, plus the build args build_image passes to it.

    PYTHON_VERSION   .python-version, else config.python_version, else 3.12
    PACKAGE_MANAGER  env.PYTHON_PACKAGE_MANAGER (install_dependencies)
    START_COMMAND    job parameter START_COMMAND, else the Procfile's web:
                     line, else "python main.py" / "python app.py"
    PORT             job parameter PORT, else 8000

  Returns: [file: <Dockerfile path>, args: [NAME: value, ...]]
*/
Map call() {
    String file = "${pwd(tmp: true)}/python.Dockerfile"
    writeFile file: file, text: resource('Dockerfile')

    return [
        file: file,
        args: [
            PYTHON_VERSION : python_version(),
            PACKAGE_MANAGER: env.PYTHON_PACKAGE_MANAGER ?: 'pip',
            START_COMMAND  : start_command(),
            PORT           : params.PORT ?: '8000',
        ]
    ]
}

String python_version() {
    if (fileExists('.python-version')) {
        List<String> lines = readFile('.python-version').trim().readLines()
        String version = lines ? lines[0].trim() : ''
        if (version ==~ /3\.\d+(\.\d+)?/) {
            return version
        }
        echo "WARNING: ignoring .python-version '${version}': expected e.g. 3.12 or 3.12.4."
    }
    return config.python_version ?: '3.12'
}

String start_command() {
    if (params.START_COMMAND) {
        return params.START_COMMAND
    }
    if (fileExists('Procfile')) {
        for (String line : readFile('Procfile').readLines()) {
            if (line.startsWith('web:')) {
                return line.substring(4).trim()
            }
        }
    }
    for (String entry : ['main.py', 'app.py']) {
        if (fileExists(entry)) {
            echo "No START_COMMAND given; starting the app with 'python ${entry}'."
            return "python ${entry}"
        }
    }
    error "Don't know how to start this app: set the START_COMMAND job parameter " +
          "(e.g. \"uvicorn main:app --host 0.0.0.0 --port \$PORT\") or add a Procfile with a web: line."
}
