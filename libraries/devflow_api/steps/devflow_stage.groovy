/*
  devflow_stage: a Jenkins stage that also tells the DevFlow backend when it
  starts, passes, fails or is skipped, so the job page can draw stage progress.

      devflow_stage('Build') { install_dependencies() }

  A step that finds nothing to do (e.g. unit_test in a repo with no tests)
  sets env.DEVFLOW_STAGE_SKIPPED to a short reason; the stage is then
  reported as "skipped" with that reason instead of "success". The flag is
  cleared at the start of every stage.

  The run is identified by env.DEVFLOW_RUN_ID (job parameter).
*/
void call(String name, Closure body) {
    stage(name) {
        env.DEVFLOW_STAGE_SKIPPED = ''
        send_status(name, 'running')
        try {
            body()
        } catch (err) {
            send_status(name, 'failed')
            throw err
        }

        String skipReason = env.DEVFLOW_STAGE_SKIPPED
        if (skipReason) {
            // The DevFlow build page reads this line from the console log to
            // show the stage as skipped (until send_status reports it).
            echo "[DevFlow] Stage skipped: ${skipReason}"
            send_status(name, 'skipped', skipReason)
        } else {
            send_status(name, 'success')
        }
    }
}

// status: running | success | failed | skipped. message: optional, the skip reason.
void send_status(String stage, String status, String message = null) {
    // TODO: POST {stage, status, message} to "${config.api_url}/pipelines/runs/${env.DEVFLOW_RUN_ID}/stages"
    //       with the token from config.token_credentials_id (httpRequest plugin or curl).
}
