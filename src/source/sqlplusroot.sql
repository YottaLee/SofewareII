set linesize 200
set term off verify off feedback off pagesize 999
set markup html on entmap ON spool on preformat off
spool C:\java\1.xlsx
select * from goods ;
spool off
exit