/*
  unit_test (Python): run the repo's own tests with pytest, inside the
  project environment install_dependencies created. pytest also collects
  unittest-style test classes. Tests are never generated.

    pytest exit 0           pass, results published to Jenkins
    pytest exit 5           no tests collected: the stage is reported to
                            DevFlow as "skipped", not as passed
    anything else           stage fails

  If the project doesn't depend on pytest, pytest==config.pytest_version is
  installed into the project's .venv for the run (its dependency files are
  left alone).

  TODO: the DevFlow backend's tests need PostgreSQL with a devflow_test
  database (see Website/backend/docker-compose.yml). Start one around the
  pytest call for that tier, e.g. docker.image('postgres:16').withRun(...).

  Needs: env.PYTHON_RUN and env.PYTHON_PIP from install_dependencies.
*/
void call() {
    ensure_pytest()

    String report = 'reports/pytest.xml'
    int rc = sh(script: "${env.PYTHON_RUN} pytest --junitxml=${report}", returnStatus: true)

    if (rc == 5) {
        String reason = 'No tests found in this repository'
        echo "${reason} (pytest looks for test_*.py and *_test.py files). Skipping unit tests."
        env.DEVFLOW_STAGE_SKIPPED = reason
        return
    }

    if (fileExists(report)) {
        junit testResults: report, allowEmptyResults: true
    }
    if (rc != 0) {
        error "Unit tests failed (pytest exit code ${rc})."
    }
}

void ensure_pytest() {
    if (sh(script: "${env.PYTHON_RUN} pytest --version > /dev/null 2>&1", returnStatus: true) == 0) {
        return
    }
    String version = config.pytest_version ?: '8.3.5'
    echo "pytest isn't one of the project's dependencies; installing pytest==${version} into .venv to run the tests."
    sh "${env.PYTHON_PIP} install --quiet pytest==${version}"
}
