# Start Firebase App Distribution on GitHub Actions (manual only)
$ErrorActionPreference = "Stop"
& "$PSScriptRoot\run-ci.ps1" -Target firebase
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
