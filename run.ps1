$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$binDir = Join-Path $projectRoot "bin"

& (Join-Path $projectRoot "build.ps1")

Write-Host "Starting game..."
java -cp $binDir App
