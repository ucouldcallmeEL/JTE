fields {
    required {
        working_dir = String        // folder holding the root module, e.g. "environments/prod"
    }
    optional {
        var_file = String
        checkov_soft_fail = Boolean // report Checkov findings without failing
    }
}
