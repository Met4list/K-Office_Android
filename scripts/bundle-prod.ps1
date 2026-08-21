# Start Play AAB build on GitHub Actions (manual only)
$ErrorActionPreference = "Stop"
& "$PSScriptRoot\run-ci.ps1" -Target play
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
