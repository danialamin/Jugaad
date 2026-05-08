# =============================================================
# Downloads the SQL Server JDBC native authentication DLL
# (required for integratedSecurity=true / Windows Authentication).
# Run once from the project root:
#   powershell -ExecutionPolicy Bypass -File lib\auth\download_auth_dll.ps1
# =============================================================

$ErrorActionPreference = "Stop"

$version = "12.8.1"
$arch    = "x64"   # change to "x86" only if you run a 32-bit JDK
$file    = "mssql-jdbc_auth-$version.$arch.dll"
$url     = "https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc_auth/$version.$arch/$file"

$targetDir = Join-Path $PSScriptRoot ""   # this script lives in lib/auth
$target    = Join-Path $targetDir $file

if (Test-Path $target) {
    Write-Host "[OK] $file already present at $target" -ForegroundColor Green
    exit 0
}

Write-Host "Downloading $file ..." -ForegroundColor Cyan
Write-Host "  $url"
Invoke-WebRequest -Uri $url -OutFile $target -UseBasicParsing
Write-Host "[OK] Saved to $target" -ForegroundColor Green
Write-Host ""
Write-Host "Now run the game with -Djava.library.path=lib\auth" -ForegroundColor Yellow
Write-Host "(or just use run.bat from the project root)." -ForegroundColor Yellow
