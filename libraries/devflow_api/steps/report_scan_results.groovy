/*
  report_scan_results: after each scanner step, upload its JSON report to the
  DevFlow backend so findings show up in the UI. A JTE lifecycle hook:
  templates never call it.

  Reports: reports/semgrep.json, reports/trivy-fs.json, reports/trivy-image.json
  context.exceptionThrown is true when the scan failed the build; still report.
*/
@AfterStep({ hookContext.step in ['semgrep_scan', 'trivy_fs_scan', 'trivy_image_scan'] })
void call(context) {
    // TODO: pick the report file for context.step and POST it to
    //       "${config.api_url}/scans" together with env.DEVFLOW_RUN_ID.
}
