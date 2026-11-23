# MySQL backup and restore (TS-001)

## 1. Scope

The script `scripts/backup-mysql.ps1` backs up the `bluemoon` database
using MySQL 8.0 `mysqldump.exe`.

- Backup filename: `bluemoon-yyyyMMdd-HHmmss.sql`.
- Backup directory: `C:\BlueMoonBackups`.
- Log file: `C:\ProgramData\BlueMoon\backup.log`.
- Last successful backup date: `C:\ProgramData\BlueMoon\last-success.txt`.
- Retains the 7 most recent successful backups.
- Uses a file lock to prevent concurrent backup processes.
- After one successful backup, further runs on the same calendar day are skipped.

## 2. Windows prerequisites and setup

Requires MySQL Server 8.0 and MySQL Client 8.0. The script expects:

```text
C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe
```

Create a dedicated backup account as a MySQL administrator:

```sql
CREATE USER IF NOT EXISTS 'bluemoon_backup'@'localhost'
IDENTIFIED BY 'REPLACE_WITH_A_PRIVATE_PASSWORD';

GRANT SELECT, SHOW VIEW, TRIGGER, EVENT ON bluemoon.*
TO 'bluemoon_backup'@'localhost';
```

If the account already exists, verify its grants rather than creating another
account. Do not use the application account or MySQL root for routine backups.

Create the directories:

```powershell
New-Item -ItemType Directory -Force C:\BlueMoonBackups
New-Item -ItemType Directory -Force C:\ProgramData\BlueMoon
```

Create `C:\ProgramData\BlueMoon\backup.cnf`:

```ini
[client]
host=localhost
port=3306
user=bluemoon_backup
password=REPLACE_WITH_PRIVATE_PASSWORD
```

Replace the password locally. This file contains a plaintext credential:
never commit it, upload it, or share it in chat. Restrict its ACL. From an
elevated PowerShell window:

```powershell
$config = "C:\ProgramData\BlueMoon\backup.cnf"
$identity = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
icacls $config /inheritance:r
icacls $config /grant:r "$($identity):(R)" "*S-1-5-18:(F)" "*S-1-5-32-544:(F)"
```

Check the resulting permissions:

```powershell
icacls C:\ProgramData\BlueMoon\backup.cnf
```

## 3. Run at Windows sign-in

Copy `scripts/start-backup.vbs.example` to
`C:\ProgramData\BlueMoon\start-backup.vbs`. Edit the copied VBS file so
the path inside it points to the local `scripts\backup-mysql.ps1`. The
example currently uses the development path
`C:\Users\Thinkpad\IdeaProjects\IT3180-NMCNPM-GR16-20261`; change it
if the repository is in another location.

Create a shortcut in the current user's Startup folder:

1. Press `Win + R`, enter `shell:startup`.
2. Create a shortcut.
3. Set its target to:

```text
C:\Windows\System32\wscript.exe "C:\ProgramData\BlueMoon\start-backup.vbs"
```

The VBS file starts PowerShell hidden and runs the script from the repository.

**Scheduling limitation:** Startup runs only when this Windows user signs in.
It is not a fixed-time daily schedule. If the user does not sign in for
several days, the next sign-in creates only one backup for that day; it does
not create one backup for every missed day. If AC-066 requires execution at
a fixed time even when nobody is signed in, configure Windows Task Scheduler
with an appropriate task identity and credentials after the team agrees.

## 4. Run manually and inspect results

Run from the repository root:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "scripts\backup-mysql.ps1"
```

Inspect the backup, log, and marker:

```powershell
Get-ChildItem C:\BlueMoonBackups
Get-Content C:\ProgramData\BlueMoon\backup.log -Tail 10
Get-Content C:\ProgramData\BlueMoon\last-success.txt
```

The SQL file must be non-empty and UTF-8. The script uses
`Start-Process -RedirectStandardOutput` to prevent Windows PowerShell 5.1
from converting native mysqldump output to UTF-16 when using `1>`.

## 5. Restore into a separate test database

Never restore a test backup over the live `bluemoon` database.

Create a separate database:

```sql
CREATE DATABASE bluemoon_restore_test
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Restore a selected backup (replace the filename):

```powershell
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" --default-character-set=utf8mb4 -u root -p bluemoon_restore_test < "C:\BlueMoonBackups\bluemoon-yyyyMMdd-HHmmss.sql"'
```

Check the restored schema:

```sql
USE bluemoon_restore_test;
SHOW TABLES;
```

Compare row counts in the source and restored databases:

```sql
SELECT 'bluemoon' AS db_name,
       (SELECT COUNT(*) FROM bluemoon.ho_gia_dinh) AS ho_gia_dinh,
       (SELECT COUNT(*) FROM bluemoon.nhan_khau) AS nhan_khau,
       (SELECT COUNT(*) FROM bluemoon.phieu_thu) AS phieu_thu
UNION ALL
SELECT 'bluemoon_restore_test',
       (SELECT COUNT(*) FROM bluemoon_restore_test.ho_gia_dinh),
       (SELECT COUNT(*) FROM bluemoon_restore_test.nhan_khau),
       (SELECT COUNT(*) FROM bluemoon_restore_test.phieu_thu);
```

The two rows should match if the source database has not changed since the
backup was taken. This is a row-count check for key tables; a deeper restore
test should compare other business tables as well.

After testing and when it is safe to do so, remove the test database:

```sql
DROP DATABASE bluemoon_restore_test;
```

## 6. Troubleshooting

- Missing `mysqldump.exe`: verify the MySQL 8.0 path in the script.
- Authentication failure: verify the MySQL backup account, grants, and
  `backup.cnf`; never put the password in the script.
- No new backup: inspect `backup.log` and `last-success.txt`; the script
  skips additional successful backups on the same day.
- Invalid dump encoding: inspect with `Format-Hex`. A valid dump begins
  with bytes `2D 2D 20` (the characters `-- `), not `FF FE`.
