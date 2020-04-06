.output filename
.dump tablename

/
alter table users add "salt" Char(50) NOT NULL default 0;
/
PRAGMA foreign_keys = on;
PRAGMA primary_keys = on;
/
update users 
set password = "xq9vgvzCgJh/iVlXISYw5eatT/s=", salt = "ODHKr8cauGI=" 
where user_id = 3

fyi
Если меняешь пропертя не надо перезапускать джарник