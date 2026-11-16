/*
  report_deployment: after helm_deploy succeeds, send the live URL and image
  to the DevFlow backend. A JTE lifecycle hook: templates never call it.
*/
@AfterStep({ hookContext.step == 'helm_deploy' })
void call(context) {
    // TODO: unless context.exceptionThrown, POST env.APP_URL and env.IMAGE_REF to
    //       "${config.api_url}/deployments" together with env.DEVFLOW_PROJECT_ID.
}
