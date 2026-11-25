# BlueMoon - TS-001
# Automatic backup of bluemoon when the current Windows user signs in.
# At most one successful backup is created per calendar day.

$ErrorActionPreference = "Stop"
$MySqlDump = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
$ConfigPath = "C:\ProgramData\BlueMoon\backup.cnf"
$BackupDir = "C:\BlueMoonBackups"
$LogPath = "C:\ProgramData\BlueMoon\backup.log"
$MarkerPath = "C:\ProgramData\BlueMoon\last-success.txt"
$LockPath = "C:\ProgramData\BlueMoon\backup.lock"
$KeepCount = 7
$Today = Get-Date -Format "yyyyMMdd"
$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$FileName = "bluemoon-$Timestamp.sql"
$FinalPath = Join-Path $BackupDir $FileName
$TempPath = "$FinalPath.tmp"
$ErrorPath = "$TempPath.err"
$LockStream = $null

function Write-BackupLog {
    param([string]$Message)
    $Time = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Add-Content -Path $LogPath -Value "[$Time] $Message"
}

try {
    New-Item -ItemType Directory -Force $BackupDir | Out-Null
    New-Item -ItemType Directory -Force (Split-Path $LogPath) | Out-Null
    if (-not (Test-Path $MySqlDump)) { throw "mysqldump.exe not found." }
    if (-not (Test-Path $ConfigPath)) { throw "backup.cnf not found." }

    try {
        $LockStream = [System.IO.File]::Open(
            $LockPath,
            [System.IO.FileMode]::OpenOrCreate,
            [System.IO.FileAccess]::ReadWrite,
            [System.IO.FileShare]::None
        )
    }
    catch {
        Write-BackupLog "BO QUA: Mot tien trinh sao luu dang chay."
        exit 0
    }

    if (Test-Path $MarkerPath) {
        $LastDate = (Get-Content $MarkerPath -Raw).Trim()
        if ($LastDate -eq $Today) {
            Write-BackupLog "BO QUA: Hom nay da sao luu thanh cong."
            exit 0
        }
    }

    Write-BackupLog "BAT DAU sao luu CSDL bluemoon."
    Remove-Item $TempPath -Force -ErrorAction SilentlyContinue
    Remove-Item $ErrorPath -Force -ErrorAction SilentlyContinue

    $Arguments = @(
        "--defaults-extra-file=$ConfigPath"
        "--default-character-set=utf8mb4"
        "--single-transaction"
        "--quick"
        "--triggers"
        "--hex-blob"
        "--no-tablespaces"
        "--set-gtid-purged=OFF"
        "bluemoon"
    )

    # Redirect native output directly to a file to avoid UTF-16 in Windows PowerShell 5.1.
    $Process = Start-Process -FilePath $MySqlDump -ArgumentList $Arguments -NoNewWindow -Wait -PassThru -RedirectStandardOutput $TempPath -RedirectStandardError $ErrorPath
    $ExitCode = $Process.ExitCode

    if ($ExitCode -ne 0) {
        $ErrorText = ""
        if (Test-Path $ErrorPath) { $ErrorText = Get-Content $ErrorPath -Raw }
        throw "mysqldump failed (exit code $ExitCode). $ErrorText"
    }
    if (-not (Test-Path $TempPath)) { throw "Backup file was not created." }
    if ((Get-Item $TempPath).Length -eq 0) { throw "Backup file is empty." }

    Move-Item $TempPath $FinalPath
    Remove-Item $ErrorPath -Force -ErrorAction SilentlyContinue
    $SizeMB = [math]::Round((Get-Item $FinalPath).Length / 1MB, 2)
    Set-Content -Path $MarkerPath -Value $Today
    Write-BackupLog "THANH CONG: $FileName ($SizeMB MB)."

    $OldBackups = Get-ChildItem $BackupDir -Filter "bluemoon-*.sql" -File |
        Sort-Object LastWriteTime -Descending |
        Select-Object -Skip $KeepCount
    foreach ($OldFile in $OldBackups) {
        Remove-Item $OldFile.FullName -Force
        Write-BackupLog "DA XOA BAN CU: $($OldFile.Name)."
    }
}
catch {
    Remove-Item $TempPath -Force -ErrorAction SilentlyContinue
    Write-BackupLog "THAT BAI: $($_.Exception.Message)"
}
finally {
    if ($null -ne $LockStream) { $LockStream.Dispose() }
}
