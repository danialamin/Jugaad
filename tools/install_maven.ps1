# =============================================================
# Downloads Apache Maven into tools/apache-maven-<ver> if missing.
# Idempotent: prints OK and exits if already installed.
# =============================================================
$ErrorActionPreference = "Stop"

$mvnVersion = "3.9.9"
$baseDir    = $PSScriptRoot
$installDir = Join-Path $baseDir "apache-maven-$mvnVersion"
$mvnCmd     = Join-Path $installDir "bin\mvn.cmd"

if (Test-Path $mvnCmd) {
    Write-Host "[OK] Maven already installed at $installDir" -ForegroundColor Green
    exit 0
}

$zipName = "apache-maven-$mvnVersion-bin.zip"
$zipPath = Join-Path $baseDir $zipName
$url     = "https://archive.apache.org/dist/maven/maven-3/$mvnVersion/binaries/$zipName"

Write-Host "Downloading Maven $mvnVersion ..." -ForegroundColor Cyan
Write-Host "  $url"
Invoke-WebRequest -Uri $url -OutFile $zipPath -UseBasicParsing

Write-Host "Extracting ..." -ForegroundColor Cyan
Expand-Archive -Path $zipPath -DestinationPath $baseDir -Force
Remove-Item $zipPath -Force

if (-not (Test-Path $mvnCmd)) {
    Write-Host "[FAIL] Maven extraction did not produce expected file: $mvnCmd" -ForegroundColor Red
    exit 1
}

Write-Host "[OK] Maven $mvnVersion installed to $installDir" -ForegroundColor Green
