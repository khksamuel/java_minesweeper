$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcDir = Join-Path $projectRoot "src"
$binDir = Join-Path $projectRoot "bin"

if (-not (Test-Path $srcDir)) {
    throw "Source directory not found: $srcDir"
}

if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

Write-Host "Compiling Java sources from $srcDir to $binDir..."
$sourceFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -File | ForEach-Object { $_.FullName }

if ($sourceFiles.Count -eq 0) {
    throw "No Java source files found in $srcDir"
}

& javac -d $binDir @sourceFiles
if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

Write-Host "Build complete."
