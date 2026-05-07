@echo off
:: CampusFlex - SQL Server Setup (Run as Administrator)
echo ============================================
echo   CampusFlex SQL Server Setup
echo   Run this as ADMINISTRATOR
echo ============================================
echo.

powershell -ExecutionPolicy Bypass -File "%~dp0setup_sqlserver.ps1"

echo.
pause
