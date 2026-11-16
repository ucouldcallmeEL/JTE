/*
  terraform_plan: plan into tfplan so terraform_apply applies exactly
  what was reviewed.
*/
void call() {
    // TODO: dir(config.working_dir) { sh 'terraform plan -input=false -out=tfplan' } (+ -var-file)
}
