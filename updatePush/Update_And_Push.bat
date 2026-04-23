@echo off
title CampusFlex - Update Context and Push
echo ========================================
echo  CampusFlex - Update Context ^& Push
echo ========================================
echo.

echo [Step 1] Updating Context Knowledge Graph...
powershell.exe -ExecutionPolicy Bypass -File "%~dp0..\Context\context\setup_graphify.ps1"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [!] Step 1 failed. The process stopped here so you can fix the error.
    echo ========================================
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [Step 2] Pushing updates to GitHub...
powershell.exe -ExecutionPolicy Bypass -File "%~dp0..\Context\pushfromhere\push.ps1"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [!] Step 2 failed. The process stopped here so you can fix the error.
    echo ========================================
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ========================================
echo All processes completed successfully!
echo ========================================
pause
