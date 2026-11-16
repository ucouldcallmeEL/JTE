/*
  run_playbook: run config.playbook against config.inventory.
  The SSH key comes from config.ssh_credentials_id; config.check_mode
  turns the run into a dry run.
*/
void call() {
    // TODO: withCredentials([sshUserPrivateKey(credentialsId: config.ssh_credentials_id, keyFileVariable: 'SSH_KEY')]) {
    //           sh "ansible-playbook -i ${config.inventory} --private-key \$SSH_KEY ${config.playbook}"
    //       }
    //       Add --extra-vars / --check --diff from config when set.
}
