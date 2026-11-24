fields {
    optional {
        package_manager = ["pip", "poetry", "pipenv"]  // default: picked from the lockfile/requirements.txt
        pytest_version = String                         // used only if the project doesn't depend on pytest; default 8.3.5
        ruff_version = String                           // used only if the agent has no ruff; default 0.11.13
        python_version = String                         // default image's Python when the repo has no .python-version; default 3.12
        uv_version = String                             // used only if the agent has no uv; default 0.7.10
        poetry_version = String                         // used only if the agent has no poetry; default 1.8.3
        pipenv_version = String                         // used only if the agent has no pipenv; default 2024.0.1
    }
}
