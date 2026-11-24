/*
  lint (Python): ruff check . runs before install_dependencies (ruff only
  reads the source, it never imports it), so broken code fails the run
  before anything is installed.

  With the repo's own ruff config (ruff.toml, .ruff.toml or [tool.ruff] in
  pyproject.toml), its rules apply. Without one, only errors that would
  break the app at runtime are checked, not style:
      E9    syntax errors
      F63   invalid comparisons, e.g. `x is "abc"`
      F7    misplaced statements, e.g. return outside a function
      F82   undefined names

  ruff comes from python_tool: the agent's copy, else ruff==config.ruff_version.
*/
void call() {
    String ruff = python_tool('ruff', config.ruff_version ?: '0.11.13')

    String select = ''
    if (has_ruff_config()) {
        echo 'Linting with the repo\'s own ruff config.'
    } else {
        echo 'No ruff config in the repo; checking only for errors that break the app (E9, F63, F7, F82).'
        select = '--select E9,F63,F7,F82'
    }

    int rc = sh(script: "${ruff} check --no-cache ${select} .", returnStatus: true)
    if (rc == 1) {
        error 'Lint failed: ruff found problems in the code (listed above).'
    }
    if (rc != 0) {
        error "Lint failed: ruff couldn't run (exit code ${rc}), e.g. an invalid ruff config."
    }
}

boolean has_ruff_config() {
    if (fileExists('ruff.toml') || fileExists('.ruff.toml')) {
        return true
    }
    return fileExists('pyproject.toml') && readFile('pyproject.toml').contains('[tool.ruff')
}
