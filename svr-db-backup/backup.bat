SET postgresql_home=C:\Program Files\PostgreSQL\9.3
CD %postgresql_home%\bin
start pg_dump > teste.backup
PAUSE