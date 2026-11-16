/*
  terraform_init: terraform init in config.working_dir.
  State lives in the GCS backend declared in the Terraform code.
*/
void call() {
    // TODO: dir(config.working_dir) { sh 'terraform init -input=false' }
}
