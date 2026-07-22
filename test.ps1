$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcDir = Join-Path $projectRoot "src"
$testDir = Join-Path $projectRoot "tests"
$binDir = Join-Path $projectRoot "bin"
$testBinDir = Join-Path $projectRoot "bin-test"
$libDir = Join-Path $projectRoot "lib"
$junitVersion = "1.10.2"
$junitJar = Join-Path $libDir "junit-platform-console-standalone-$junitVersion.jar"
$junitUrl = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$junitVersion/junit-platform-console-standalone-$junitVersion.jar"

if (-not (Test-Path $srcDir)) {
    throw "Source directory not found: $srcDir"
}

if (-not (Test-Path $testDir)) {
    throw "Test directory not found: $testDir"
}

if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

if (-not (Test-Path $testBinDir)) {
    New-Item -ItemType Directory -Path $testBinDir | Out-Null
}

if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
}

if (-not (Test-Path $junitJar)) {
    Write-Host "Downloading JUnit platform console standalone $junitVersion..."
    Invoke-WebRequest -Uri $junitUrl -OutFile $junitJar
}

Write-Host "Compiling source files..."
$sourceFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -File | ForEach-Object { $_.FullName }
if ($sourceFiles.Count -eq 0) {
    throw "No Java source files found in $srcDir"
}

& javac -d $binDir @sourceFiles
if ($LASTEXITCODE -ne 0) {
    throw "javac failed while compiling source files."
}

Write-Host "Compiling test files..."
$testFiles = Get-ChildItem -Path $testDir -Filter "*.java" -File | ForEach-Object { $_.FullName }
if ($testFiles.Count -eq 0) {
    throw "No Java test files found in $testDir"
}

$compileCp = "$junitJar;$binDir"
& javac -cp $compileCp -d $testBinDir @testFiles
if ($LASTEXITCODE -ne 0) {
    throw "javac failed while compiling test files."
}

Write-Host "Running tests..."
$runtimeCp = "$binDir;$testBinDir"
& java -jar $junitJar --class-path $runtimeCp --scan-class-path
if ($LASTEXITCODE -ne 0) {
    throw "Tests failed."
}

Write-Host "All tests passed."
