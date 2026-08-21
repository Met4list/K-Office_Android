# Зібрати підписаний prodRelease AAB і покласти в dist/
$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

& .\gradlew.bat :app:bundlePlay
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$dist = Join-Path (Get-Location) "dist"
if (Test-Path $dist) {
    Invoke-Item $dist
}
