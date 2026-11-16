// Tier: Jenkins folder "infrastructure/ansible". Configures k3s, Jenkins and Traefik hosts.
libraries {
    ansible {
        playbook = 'site.yml'                       // TODO
        inventory = 'inventories/prod/gcp.yml'      // TODO: gcp_compute dynamic inventory
        ssh_credentials_id = 'ansible-ssh'
    }
}
