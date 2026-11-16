/*
  devflow_stage: a Jenkins stage that also tells the DevFlow backend when it
  starts, passes and fails, so the job page can draw stage progress.

      devflow_stage('Build') { install_dependencies() }

  The run is identified by env.DEVFLOW_RUN_ID (job parameter).
*/
void call(String name, Closure body) {
    stage(name) {
        send_status(name, 'running')
        try {
            body()
            send_status(name, 'success')
        } catch (err) {
            send_status(name, 'failed')
            throw err
        }
    }
}

void send_status(String stage, String status) {
    // TODO: POST {stage, status} to "${config.api_url}/pipelines/runs/${env.DEVFLOW_RUN_ID}/stages"
    //       with the token from config.token_credentials_id (httpRequest plugin or curl).
}
