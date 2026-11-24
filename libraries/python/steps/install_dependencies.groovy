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

  uv/poetry/pipenv come from python_tool: the agent's copy, else the pinned
  config version in a tools venv, never in the project's .venv.

  Sets: env.PYTHON_PACKAGE_MANAGER  "uv" | "poetry" | "pipenv" | "pip"
        env.PYTHON_RUN              prefix lint/unit_test put before a tool,
                                    e.g. sh "${env.PYTHON_RUN} pytest"
        env.PYTHON_PIP              pip command that installs into .venv,
                                    e.g. sh "${env.PYTHON_PIP} install pytest"
*/
void call() {
    String manager = config.package_manager ?: detect_package_manager()
    env.PYTHON_PACKAGE_MANAGER = manager
    echo "Installing Python dependencies with ${manager}"

    // Always start from a fresh .venv. One left by an earlier build can be
    // broken: its scripts (pip, pytest, ...) start with the absolute path of
    // the venv's python, which stops existing when Jenkins renames the job
    // and moves its workspace ("not found", exit code 127).
    sh 'rm -rf .venv'

    switch (manager) {
        case 'uv':
            String uv = python_tool('uv', config.uv_version ?: '0.7.10')
            sh "${uv} sync --locked"
            env.PYTHON_RUN = "${uv} run"
            env.PYTHON_PIP = "${uv} pip"    // uv venvs have no pip of their own
            break

        case 'poetry':
            String poetry = python_tool('poetry', config.poetry_version ?: '1.8.3')
            withEnv(['POETRY_VIRTUALENVS_IN_PROJECT=true']) {
                sh "${poetry} install --no-interaction"
            }
            env.PYTHON_RUN = "${poetry} run"
            break

        case 'pipenv':
            String pipenv = python_tool('pipenv', config.pipenv_version ?: '2024.0.1')
            String install = fileExists('Pipfile.lock') ? 'sync --dev' : 'install --dev'
            withEnv(['PIPENV_VENV_IN_PROJECT=1']) {
                sh "${pipenv} ${install}"
            }
            env.PYTHON_RUN = "${pipenv} run"
            break

        case 'pip':
            sh 'python3 -m venv .venv'
            if (fileExists('requirements.txt')) {
                sh '.venv/bin/python -m pip install -r requirements.txt'
                if (fileExists('requirements-dev.txt')) {
                    sh '.venv/bin/python -m pip install -r requirements-dev.txt'
                }
            } else {
                sh '.venv/bin/python -m pip install .'
            }
            env.PYTHON_RUN = '.venv/bin/python -m'
            break
        case 'none':
            sh 'python3 -m venv .venv'
            env.PYTHON_RUN = '.venv/bin/python -m'
            break
    }
    if (!env.PYTHON_PIP) {
        env.PYTHON_PIP = '.venv/bin/python -m pip'
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
    echo 'WARNING: no dependency file found (uv.lock, poetry.lock, Pipfile, requirements.txt, pyproject.toml). ' +
     'Treating this as a standard-library-only app.'
    return 'none'
}
