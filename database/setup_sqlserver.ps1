# =============================================================
# CampusFlex - SQL Server Setup Script
# Run this script AS ADMINISTRATOR on any machine with SQL Server Express installed.
# It configures TCP/IP, enables SQL auth, creates the login and database.
# =============================================================

Write-Host "=== CampusFlex SQL Server Setup ===" -ForegroundColor Cyan

# Step 1: Find the SQL Server instance
$instances = Get-Service -Name "MSSQL`$*" -ErrorAction SilentlyContinue
if (-not $instances) {
    Write-Host "ERROR: No SQL Server instance found. Install SQL Server Express first." -ForegroundColor Red
    Write-Host "Download: https://www.microsoft.com/en-us/sql-server/sql-server-downloads" -ForegroundColor Yellow
    exit 1
}

$instance = ($instances | Select-Object -First 1).Name
$instanceName = $instance -replace "MSSQL\$", ""
Write-Host "Found SQL Server instance: $instanceName" -ForegroundColor Green

# Step 2: Find the registry path
$regPaths = Get-ChildItem "HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server" | Where-Object { $_.PSChildName -match "MSSQL\d+\.$instanceName" }
if (-not $regPaths) {
    Write-Host "ERROR: Could not find registry path for instance $instanceName" -ForegroundColor Red
    exit 1
}
$sqlRegPath = $regPaths[0].PSPath
Write-Host "Registry path: $($regPaths[0].PSChildName)" -ForegroundColor Gray

# Step 3: Enable TCP/IP protocol
$tcpPath = "$sqlRegPath\MSSQLServer\SuperSocketNetLib\Tcp"
Set-ItemProperty -Path $tcpPath -Name "Enabled" -Value 1
Write-Host "[OK] TCP/IP protocol enabled" -ForegroundColor Green

# Step 4: Set static port 1433
$tcpAllPath = "$tcpPath\IPAll"
Set-ItemProperty -Path $tcpAllPath -Name "TcpPort" -Value "1433"
Set-ItemProperty -Path $tcpAllPath -Name "TcpDynamicPorts" -Value ""
Write-Host "[OK] TCP port set to 1433" -ForegroundColor Green

# Step 5: Enable mixed mode authentication (SQL + Windows)
$mssqlServerPath = "$sqlRegPath\MSSQLServer"
Set-ItemProperty -Path $mssqlServerPath -Name "LoginMode" -Value 2
Write-Host "[OK] Mixed mode authentication enabled" -ForegroundColor Green

# Step 6: Restart SQL Server
Write-Host "Restarting SQL Server..." -ForegroundColor Yellow
Restart-Service $instance -Force
Start-Sleep -Seconds 3
Write-Host "[OK] SQL Server restarted" -ForegroundColor Green

# Step 7: Enable sa login and set password
$saPassword = "CampusFlex123!"
try {
    sqlcmd -S "localhost,1433" -E -Q "ALTER LOGIN sa WITH PASSWORD = '$saPassword'; ALTER LOGIN sa ENABLE;" 2>&1 | Out-Null
    Write-Host "[OK] sa login enabled (password: $saPassword)" -ForegroundColor Green
} catch {
    Write-Host "WARNING: Could not configure sa login. You may need to do this manually in SSMS." -ForegroundColor Yellow
}

# Step 8: Create database
try {
    $result = sqlcmd -S "localhost,1433" -U sa -P $saPassword -Q "IF NOT EXISTS (SELECT * FROM sys.databases WHERE name='campusFlexDb') CREATE DATABASE campusFlexDb" 2>&1
    Write-Host "[OK] Database 'campusFlexDb' ready" -ForegroundColor Green
} catch {
    Write-Host "WARNING: Could not create database automatically." -ForegroundColor Yellow
}

# Step 9: Verify connection
Write-Host ""
Write-Host "=== Verifying Connection ===" -ForegroundColor Cyan
$testResult = Test-NetConnection -ComputerName localhost -Port 1433 -WarningAction SilentlyContinue
if ($testResult.TcpTestSucceeded) {
    Write-Host "[OK] SQL Server is accessible on localhost:1433" -ForegroundColor Green
} else {
    Write-Host "FAIL: Port 1433 not reachable. Check Windows Firewall." -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Setup Complete ===" -ForegroundColor Cyan
Write-Host "Connection details:" -ForegroundColor White
Write-Host "  Server:   localhost:1433" -ForegroundColor White
Write-Host "  Database: campusFlexDb" -ForegroundColor White
Write-Host "  User:     sa" -ForegroundColor White
Write-Host "  Password: $saPassword" -ForegroundColor White
Write-Host ""
Write-Host "You can now run CampusFlex and use the SQL Server save option!" -ForegroundColor Green
