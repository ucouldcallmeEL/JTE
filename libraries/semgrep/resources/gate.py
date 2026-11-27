"""
Semgrep gate, run by semgrep_scan after the scan.

    python3 gate.py <semgrep json report> <fail: yes|no>

Prints a count per severity and lists the findings that fail the build.
ERROR fails (and CRITICAL/HIGH, the newer severity names some registry rules
use); WARNING, MEDIUM, LOW and INFO are reported only.
Exit status: 0 = pass, 3 = failing findings (and fail = yes).
"""
import json
import sys
from collections import Counter

FAILING = {"ERROR", "CRITICAL", "HIGH"}

report_path, fail = sys.argv[1], sys.argv[2] == "yes"
with open(report_path, encoding="utf-8") as f:
    results = json.load(f).get("results", [])

severity = lambda r: str(r.get("extra", {}).get("severity", "UNKNOWN")).upper()
counts = Counter(severity(r) for r in results)
failing = [r for r in results if severity(r) in FAILING]

summary = ", ".join(f"{n} {s}" for s, n in sorted(counts.items())) or "no findings"
print(f"Semgrep: {summary}.")

if failing:
    print(f"\n{len(failing)} finding(s) at ERROR level:")
    for r in failing:
        print(f"  {r['path']}:{r['start']['line']}  {r['check_id']}")
    print("\nFix them, or if one is a false positive, add '# nosemgrep: <rule id>' on that line.")
    if fail:
        sys.exit(3)
    print("Not failing: fail_on_findings = false.")
