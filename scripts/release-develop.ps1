# Commit local source changes, push develop, start Play AAB on GitHub Actions
param(
    [string]$Message = "Prepare Play release from develop.",
    [ValidateSet("play", "firebase")]
    [string]$Target = "play",
    [switch]$SkipCommit
)

$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw "git is missing"
}
if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    throw "GitHub CLI (gh) is missing. Install it and run: gh auth login"
}

if (-not $SkipCommit) {
    # Do not commit IDE caches, local secrets, or Kotlin daemon files
    git add -A -- .
    git reset --quiet -- `
        ".idea" `
        ".kotlin" `
        "local.properties" `
        "secrets.properties" `
        "app/google-services.json" `
        "dist" `
        2>$null

    $staged = git diff --cached --name-only
    if ($staged) {
        Write-Host "Commit:"
        $staged | ForEach-Object { Write-Host "  $_" }
        git commit -m $Message
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    } else {
        Write-Host "Nothing to commit, pushing current develop."
        git reset --quiet
    }
}

$branch = (git rev-parse --abbrev-ref HEAD).Trim()
if ($branch -ne "develop") {
    throw "Current branch is '$branch'. Checkout develop first."
}

Write-Host "Push develop..."
git push origin develop
if ($LASTEXITCODE -ne 0) {
    $token = gh auth token
    if (-not $token) { throw "git push failed and gh auth token is empty" }
    git push "https://x-access-token:$token@github.com/Met4list/K-Office_Android.git" develop
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

& "$PSScriptRoot\run-ci.ps1" -Target $Target
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
