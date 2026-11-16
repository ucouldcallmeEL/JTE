fields {
    required {
        playbook = String               // path in the infra repo, e.g. "site.yml"
        inventory = String              // e.g. "inventories/prod/gcp.yml"
        ssh_credentials_id = String     // Jenkins SSH key credential for the hosts
    }
    optional {
        extra_vars = String             // passed to --extra-vars as-is
        check_mode = Boolean            // dry run: --check --diff
    }
}
