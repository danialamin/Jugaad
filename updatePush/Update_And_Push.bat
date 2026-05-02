@echo off
REM CampusFlex Safe Team Push Tool
cls

cd /d "%~dp0.."

    echo ========================================
echo  CampusFlex Safe Team Push Tool
echo ========================================
echo.

REM Automatically ignore .vs folder permanently
if not exist ".gitignore" (
    echo .vs/> .gitignore
) else (
    findstr /c:".vs/" ".gitignore" >nul
    if errorlevel 1 echo .vs/>> .gitignore
)

REM Force Git to forget the .vs folder if it was already tracked
if exist ".vs" (
    git rm -r --cached .vs >nul 2>&1
)

REM Detect current branch
for /f "tokens=*" %%a in ('git branch --show-current') do set BRANCH=%%a
if "%BRANCH%"=="" set BRANCH=main
echo Target Branch: %BRANCH%

set /p user="Who is pushing? (A: Abdullah Aamir, D: Danial Amin, X: Dyen Asif): "

if /i "%user%"=="A" (
    set name=Abdullah Aamir
) else if /i "%user%"=="D" (
    set name=Danial Amin
) else if /i "%user%"=="X" (
    set name=Dyen Asif
) else (
    set name=Unknown Member
)

echo.
echo [1/4] Staging all files...
git add -A

echo.
echo [2/4] Creating commit...
set /p customMsg="Add to commit message? (Press ENTER for default): "

if "%customMsg%"=="" (
    set commitMsg=Update project files by %name%
) else (
    set commitMsg=%customMsg% (by %name%)
)

git commit -m "%commitMsg%"

echo.
echo [3/4] Pulling latest changes from GitHub...
git pull origin %BRANCH%

echo.
echo [4/4] Pushing to GitHub...
git push origin %BRANCH%

if errorlevel 1 (
    echo.
    echo ========================================
    echo  Push Failed [ERROR]
    echo ========================================
) else (
    echo.
    echo ========================================
    echo  Push Successful [OK]
    echo  Branch pushed: %BRANCH%
    echo ========================================
)

echo.
pause
