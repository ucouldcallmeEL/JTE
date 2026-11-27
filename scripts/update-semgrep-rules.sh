#!/usr/bin/env sh
# Refresh the Semgrep rules kept in this repo (libraries/semgrep/resources/rules/)
# from the Semgrep Registry. Builds never download rules; they use these files.
#
#   scripts/update-semgrep-rules.sh             # refreshes p/default
#   scripts/update-semgrep-rules.sh python      # adds/refreshes p/python as python.yml
#
# A new ruleset also has to be listed in the semgrep library's `rulesets` config.
# Review the git diff before committing: it shows exactly which rules changed.
set -eu
cd "$(dirname "$0")/.."
dir=libraries/semgrep/resources/rules
mkdir -p "$dir"
[ $# -eq 0 ] && set -- default

for name do
    url="https://semgrep.dev/c/p/${name}"
    out="${dir}/${name}.yml"
    tmp="${out}.tmp"
    {
        printf '# Semgrep Registry ruleset p/%s, kept in this repo so builds are\n' "$name"
        printf '# reproducible and offline. Source: %s\n' "$url"
        printf '# Downloaded %s by scripts/update-semgrep-rules.sh. Do not edit by hand.\n' "$(date -u +%Y-%m-%d)"
        curl -fsSL "$url"
    } > "$tmp"

    if command -v semgrep >/dev/null 2>&1; then
        semgrep --validate --metrics=off --config "$tmp" >/dev/null
    else
        echo "semgrep not installed here: ${out} not validated (the Jenkins container can: semgrep --validate --config ...)" >&2
    fi
    mv "$tmp" "$out"
    echo "Updated ${out}: $(grep -c '^- id:' "$out") rules"
done
