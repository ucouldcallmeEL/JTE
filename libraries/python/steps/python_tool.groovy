/*
  python_tool: the command to run a Python CLI tool (uv, poetry, pipenv,
  ruff): the agent's own copy if it has one, otherwise name==version
  installed into a tools venv in the workspace's tmp dir, never into the
  project's .venv.

      String ruff = python_tool('ruff', '0.11.13')
      sh "${ruff} check ."
*/
String call(String name, String version) {
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
