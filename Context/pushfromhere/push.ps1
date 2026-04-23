# Safe Git Push Script for Team Collaboration
Set-Location -Path "$PSScriptRoot\..\.."

# 1. Check if Git is installed
try {
    $null = git --version 2>&1
} catch {
    Write-Host ""
    Write-Host "================[ ERROR: Git Missing ]================" -ForegroundColor Red
    Write-Host "It looks like Git is not installed or not added to your system PATH." -ForegroundColor Yellow
    Write-Host "Fix: Please download Git from git-scm.com and install it." -ForegroundColor Yellow
    Write-Host "======================================================" -ForegroundColor Red
    Exit 1
}

# 2. Check if it's a Git repository
if (-not (Test-Path -Path ".git")) {
    Write-Host ""
    Write-Host "================[ ERROR: Not a Git Repo! ]================" -ForegroundColor Red
    Write-Host "The '.git' folder is missing! Did you download the code as a ZIP file?" -ForegroundColor Yellow
    Write-Host "Fix: You must use 'git clone' to download the repository so you can push." -ForegroundColor Cyan
    Write-Host "==========================================================" -ForegroundColor Red
    Exit 1
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " CampusFlex Safe Team Push Tool" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$user = Read-Host "Who is pushing? (A: Abdullah Aamir, D: Danial Amin, X: Dyen Asif)"
$name = "Unknown Member"
$email = "unknown@campusflex.local"

if ($user -eq "A" -or $user -eq "a") {
    $name = "Abdullah Aamir"
    $email = "abdullah@campusflex.local"
} elseif ($user -eq "D" -or $user -eq "d") {
    $name = "Danial Amin"
    $email = "danial@campusflex.local"
} elseif ($user -eq "X" -or $user -eq "x") {
    $name = "Dyen Asif"
    $email = "dyen@campusflex.local"
} else {
    Write-Host "Invalid selection, using 'Unknown Member'" -ForegroundColor Yellow
}

# 3. Configure Git Identity for this repo
git config user.name $name
git config user.email $email

# 4. Check for stuck rebase
if ((Test-Path -Path ".git\rebase-merge") -or (Test-Path -Path ".git\rebase-apply")) {
    Write-Host ""
    Write-Host "================[ WARNING: Stuck in Rebase ]================" -ForegroundColor Yellow
    Write-Host "You are stuck in a previous merge conflict!" -ForegroundColor Red
    $abort = Read-Host "Do you want to cancel the old merge and start fresh? (Y/N)"
    if ($abort -eq "Y" -or $abort -eq "y") {
        git rebase --abort
        Write-Host "Old rebase aborted. Proceeding..." -ForegroundColor Green
    } else {
        Write-Host "Fix: Please resolve your merge conflict in your IDE, then run 'git rebase --continue'." -ForegroundColor Cyan
        Exit 1
    }
}

$customMsg = Read-Host "Enter an optional commit message (Press Enter for default)"
if ([string]::IsNullOrWhiteSpace($customMsg)) {
    $commitMsg = "Update project files by $name"
} else {
    $commitMsg = "$customMsg (by $name)"
}

# 5. Check if there are changes to commit
Write-Host "`n[1/4] Checking for changes..." -ForegroundColor Green
$gitStatus = git status --porcelain
if ([string]::IsNullOrWhiteSpace($gitStatus)) {
    Write-Host "No local changes detected. Skipping commit phase." -ForegroundColor Yellow
} else {
    Write-Host "[2/4] Staging and Committing changes..." -ForegroundColor Green
    git add .
    git commit -m $commitMsg | Out-Null
}

Write-Host "[3/4] Fetching and integrating teammates' updates..." -ForegroundColor Green
$pullResult = git pull --rebase origin main 2>&1
if ($LASTEXITCODE -ne 0) {
    if ($pullResult -match "resolve" -or $pullResult -match "CONFLICT") {
        Write-Host ""
        Write-Host "================[ ERROR: Merge Conflict! ]================" -ForegroundColor Red
        Write-Host "Wait! Your teammate edited the EXACT SAME lines of code as you did." -ForegroundColor Yellow
        Write-Host "Git paused the push so you don't accidentally delete their code." -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Fix: Open your IDE, look for the highlighted conflict areas," -ForegroundColor Cyan
        Write-Host "choose which code to keep, and then manually commit the results." -ForegroundColor Cyan
        Write-Host "==========================================================" -ForegroundColor Red
        Exit 1
    } else {
        Write-Host ""
        Write-Host "================[ ERROR: Network/Access Issue ]================" -ForegroundColor Red
        Write-Host "Failed to connect to GitHub during pull." -ForegroundColor Yellow
        Write-Host $pullResult
        Write-Host "Fix: Check your internet connection or repository permissions." -ForegroundColor Cyan
        Write-Host "===============================================================" -ForegroundColor Red
        Exit 1
    }
}

Write-Host "[4/4] Pushing to GitHub..." -ForegroundColor Green
$pushResult = git push origin main 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "================[ ERROR: Push Failed ]================" -ForegroundColor Red
    Write-Host "Failed to push to GitHub. " -ForegroundColor Yellow
    Write-Host $pushResult
    Write-Host "Fix: Make sure you have an internet connection and proper permissions." -ForegroundColor Cyan
    Write-Host "======================================================" -ForegroundColor Red
    Exit 1
}

Write-Host "`nDone! Successfully pushed to GitHub!" -ForegroundColor Cyan
Exit 0
