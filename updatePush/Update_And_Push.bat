@echo off
title CampusFlex - Update Context and Push
echo ========================================
echo  CampusFlex - Update Context ^& Push
echo ========================================
echo.

echo [Step 1] Updating Context Knowledge Graph...
powershell.exe -ExecutionPolicy Bypass -File "%~dp0..\Context\context\setup_graphify.ps1"

echo.
echo [Step 2] Pushing updates to GitHub...
powershell.exe -ExecutionPolicy Bypass -File "%~dp0..\Context\pushfromhere\push.ps1"

echo.
echo ========================================
echo All processes completed!
echo ========================================
pause
