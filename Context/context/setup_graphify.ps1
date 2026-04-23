# Graphify Installation and Execution Script
Set-Location -Path "$PSScriptRoot\..\.."

# Check if Python is installed
try {
    $null = python --version 2>&1
} catch {
    Write-Host ""
    Write-Host "================[ ERROR: Python Missing ]================" -ForegroundColor Red
    Write-Host "It looks like Python is not installed or not added to your system PATH." -ForegroundColor Yellow
    Write-Host "Fix: Please download Python from python.org and make sure" -ForegroundColor Yellow
    Write-Host "'Add Python to PATH' is checked during installation." -ForegroundColor Yellow
    Write-Host "=========================================================" -ForegroundColor Red
    Exit 1
}

echo "Generating Graphify Knowledge Graph for CampusFlex..."
$graphifyResult = python -m graphify update . 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "================[ ERROR: Graphify Missing ]================" -ForegroundColor Red
    Write-Host "Graphify failed to run. You or your teammate likely need to install it first!" -ForegroundColor Yellow
    Write-Host "Fix: Please open your terminal and run this exact command:" -ForegroundColor Yellow
    Write-Host "python -m pip install graphifyy" -ForegroundColor Cyan
    Write-Host "===========================================================" -ForegroundColor Red
    Exit 1
}

# Move the output folder inside the context folder for cleanliness
if (Test-Path -Path ".\graphify-out") {
    try {
        if (Test-Path -Path ".\Context\context\graphify-out") {
            Remove-Item -Recurse -Force ".\Context\context\graphify-out" -ErrorAction Stop
        }
        Move-Item -Path ".\graphify-out" -Destination ".\Context\context\graphify-out" -Force -ErrorAction Stop
        echo "Graphify output moved to Context/context/graphify-out."
    } catch {
        Write-Host ""
        Write-Host "================[ ERROR: File Locked ]================" -ForegroundColor Red
        Write-Host "Windows is blocking us from moving the graphify output." -ForegroundColor Yellow
        Write-Host "Fix: Do you have 'graph.html' open in your web browser?" -ForegroundColor Cyan
        Write-Host "Please close the browser tab and run this script again." -ForegroundColor Cyan
        Write-Host "======================================================" -ForegroundColor Red
        Exit 1
    }
}

echo "Graphify generation complete! The context graph is ready."
Exit 0
