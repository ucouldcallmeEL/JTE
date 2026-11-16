/*
  has_changes: true when this build's commits touch any file under the
  given paths, e.g. has_changes('backend/'). Lets the Website monorepo's
  backend and frontend jobs skip builds the other side triggered.

  Hint: walk currentBuild.changeSets -> items -> affectedPaths.
  Treat a build with no change sets (first build, manual run) as changed.
*/
boolean call(String... paths) {
    // TODO
    return true
}
