@echo off
set DATE=%date:~-4%%date:~3,2%%date:~0,2%
SET BACKUP_DIR=C:\Users\vinicius\ambiente_trabalho\backup
set FILE_NAME=svr_db_%DATE%.backup
SET PGPASSWORD=@postgresql15
pg_dump -i -h localhost -p 5432 -U postgres -F c -b -v -f %BACKUP_DIR%\%FILE_NAME% svr_desenvolvimento
echo.
echo.
echo ---------------------------------------------------------------
echo ARQUIVO: %FILE_NAME%
echo PASTA  : %BACKUP_DIR%
echo ---------------------------------------------------------------
pause