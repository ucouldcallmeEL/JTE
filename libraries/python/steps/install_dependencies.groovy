/*
  install_dependencies (Python): install the app and its dev tools into .venv
  with the tool matching the repo's dependency files. config.package_manager
  skips detection.

    uv.lock                 uv sync --locked
    poetry.lock             poetry install --no-interaction
    Pipfile(.lock)          pipenv sync --dev / pipenv install --dev
    requirements.txt        pip install -r requirements.txt (+ requirements-dev.txt)
    pyproject.toml only     pip install .
    none                    error, naming the files looked for

  A missing uv/poetry/pipenv is installed at the pinned config version into
  a tools venv in the workspace's tmp dir, never into the project's .venv.

  Sets: env.PYTHON_PACKAGE_MANAGER  "uv" | "poetry" | "pipenv" | "pip"
        env.PYTHON_RUN              prefix lint/unit_test put before a tool,
                                    e.g. sh "${env.PYTHON_RUN} pytest"
*/
void call() {
    String manager = config.package_manager ?: detect_package_manager()
    env.PYTHON_PACKAGE_MANAGER = manager
    echo "Installing Python dependencies with ${manager}"

    switch (manager) {
        case 'uv':
            String uv = resolve_tool('uv', config.uv_version ?: '0.7.10')
            sh "${uv} sync --locked"
            env.PYTHON_RUN = "${uv} run"
            break

        case 'poetry':
            String poetry = resolve_tool('poetry', config.poetry_version ?: '1.8.3')
            withEnv(['POETRY_VIRTUALENVS_IN_PROJECT=true']) {
                sh "${poetry} install --no-interaction"
            }
            env.PYTHON_RUN = "${poetry} run"
            break

        case 'pipenv':
            String pipenv = resolve_tool('pipenv', config.pipenv_version ?: '2024.0.1')
            String install = fileExists('Pipfile.lock') ? 'sync --dev' : 'install --dev'
            withEnv(['PIPENV_VENV_IN_PROJECT=1']) {
                sh "${pipenv} ${install}"
            }
            env.PYTHON_RUN = "${pipenv} run"
            break

        case 'pip':
            sh 'python3 -m venv .venv'
            if (fileExists('requirements.txt')) {
                sh '.venv/bin/pip install -r requirements.txt'
                if (fileExists('requirements-dev.txt')) {
                    sh '.venv/bin/pip install -r requirements-dev.txt'
                }
            } else {
                sh '.venv/bin/pip install .'
            }
            env.PYTHON_RUN = '.venv/bin/python -m'
            break
    }
}

String detect_package_manager() {
    if (fileExists('uv.lock')) {
        return 'uv'
    }
    if (fileExists('poetry.lock')) {
        return 'poetry'
    }
    if (fileExists('Pipfile')) {
        return 'pipenv'
    }
    if (fileExists('requirements.txt') || fileExists('pyproject.toml')) {
        return 'pip'
    }
    error 'No Python dependency file found. Looked for uv.lock, poetry.lock, Pipfile, requirements.txt and pyproject.toml.'
}

// Returns the command to run `name`: the agent's own copy if it has one,
// otherwise a pinned copy installed into a tools venv outside the repo.
String resolve_tool(String name, String version) {
    if (sh(script: "command -v ${name} > /dev/null", returnStatus: true) == 0) {
        return name
    }

    String toolsDir = "${pwd(tmp: true)}/python-tools"
    echo "${name} isn't installed on the agent; installing ${name}==${version}"
    int status = sh(
        script: """
            python3 -m venv "${toolsDir}"
            "${toolsDir}/bin/pip" install --quiet ${name}==${version}
        """,
        returnStatus: true
    )
    if (status != 0) {
        error "${name} isn't installed on the agent and installing ${name}==${version} failed."
    }
    return "\"${toolsDir}/bin/${name}\""
}
