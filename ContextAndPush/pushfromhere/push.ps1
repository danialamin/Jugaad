# Safe Git Push Script for Team Collaboration
# This script ensures that local changes are committed and safely integrated with
# updates from other team members on the remote repository without overwriting them.

Set-Location -Path "$PSScriptRoot\..\.."

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " CampusFlex Safe Team Push Tool" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$user = Read-Host "Who is pushing? (A: Abdullah Aamir, D: Danial Amin, X: Dyen Asif)"
$name = "Unknown Member"

if ($user -eq "A" -or $user -eq "a") {
    $name = "Abdullah Aamir"
} elseif ($user -eq "D" -or $user -eq "d") {
    $name = "Danial Amin"
} elseif ($user -eq "X" -or $user -eq "x") {
    $name = "Dyen Asif"
} else {
    Write-Host "Invalid selection, using 'Unknown Member'" -ForegroundColor Yellow
}

$customMsg = Read-Host "Enter an optional commit message (Press Enter for default)"
if ([string]::IsNullOrWhiteSpace($customMsg)) {
    $commitMsg = "Update project files by $name"
} else {
    $commitMsg = "$customMsg (by $name)"
}

Write-Host "`n[1/4] Staging changes..." -ForegroundColor Green
git add .

Write-Host "[2/4] Committing changes..." -ForegroundColor Green
git commit -m $commitMsg

Write-Host "[3/4] Fetching and integrating teammates' updates..." -ForegroundColor Green
# Using --rebase ensures your local commits are placed ON TOP of any new commits from GitHub.
# This prevents your push from reverting someone else's recent work!
$pullResult = git pull --rebase origin main 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error during pull! You might have a merge conflict." -ForegroundColor Red
    Write-Host $pullResult
    Write-Host "Please resolve conflicts manually before pushing." -ForegroundColor Yellow
    Exit
}

Write-Host "[4/4] Pushing to GitHub..." -ForegroundColor Green
git push origin main

Write-Host "`nDone! Successfully pushed to GitHub!" -ForegroundColor Cyan
