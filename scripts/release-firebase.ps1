# Commit source changes, push develop, start prodRelease on Firebase App Distribution (GitHub Actions)
param(
    [string]$Message = "Ship testers build to Firebase App Distribution."
)

$ErrorActionPreference = "Stop"
& "$PSScriptRoot\release-develop.ps1" -Target firebase -Message $Message
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
