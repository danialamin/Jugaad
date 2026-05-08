@echo off
REM =============================================================
REM CampusFlex - run the game with SQL Server (Hibernate ORM) save/load.
REM Uses Windows Authentication via the native auth DLL.
REM Bootstraps Maven on first run so all Hibernate deps are pulled.
REM =============================================================

setlocal EnableDelayedExpansion
cd /d "%~dp0"

REM 1. Ensure the native SQL Server auth DLL is present (Windows authentication).
if not exist "lib\auth\mssql-jdbc_auth-12.8.1.x64.dll" (
    echo [setup] Fetching SQL Server JDBC auth DLL...
    powershell -NoProfile -ExecutionPolicy Bypass -File "lib\auth\download_auth_dll.ps1"
    if errorlevel 1 goto :error
)

REM 2. Ensure Maven is installed locally under tools\.
if not exist "tools\apache-maven-3.9.9\bin\mvn.cmd" (
    echo [setup] Installing Apache Maven locally [one-time, ~10MB]...
    powershell -NoProfile -ExecutionPolicy Bypass -File "tools\install_maven.ps1"
    if errorlevel 1 goto :error
)
set "MVN=%CD%\tools\apache-maven-3.9.9\bin\mvn.cmd"

REM 3. Compile via Maven (resolves Hibernate + transitive deps from Maven Central).
echo [build] Compiling sources via Maven...
call "%MVN%" -q -DskipTests compile
if errorlevel 1 goto :error

REM 4. Build runtime classpath (project deps) into target\cp.txt.
echo [build] Resolving runtime classpath...
call "%MVN%" -q dependency:build-classpath -Dmdep.outputFile=target\cp.txt -Dmdep.excludeScope=test
if errorlevel 1 goto :error

REM 5. Launch via PowerShell to bypass cmd's ~1024 char variable limit
REM    (the resolved classpath is several KB long).
echo [run] Launching engine.GameEngine ...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$deps = (Get-Content -Raw target\cp.txt).Trim();" ^
  "$cp = 'target\classes;' + $deps + ';lib\mssql-jdbc-12.8.1.jre11.jar';" ^
  "& java '-Djava.library.path=lib\auth' '-cp' $cp 'engine.GameEngine'"
goto :eof

:error
echo.
echo Build/run failed. See messages above.
exit /b 1
