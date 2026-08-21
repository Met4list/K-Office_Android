# Start a GitHub Actions build (no local Gradle). Target: firebase | play
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("firebase", "play")]
    [string]$Target
)

$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    throw "GitHub CLI (gh) is missing. Install it and run: gh auth login"
}

$workflow = if ($Target -eq "firebase") {
    "distribute-firebase.yml"
} else {
    "bundle-prod.yml"
}

Write-Host "Starting GitHub Actions: $workflow on develop"
gh workflow run $workflow --ref develop
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Start-Sleep -Seconds 3
gh run list --workflow $workflow --branch develop --limit 3
Write-Host "https://github.com/Met4list/K-Office_Android/actions"
