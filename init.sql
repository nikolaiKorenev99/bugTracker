--insert into Users values(1,'root','', 'root@gmai.com');
--insert into Users values(2,'user1','', 'user1@gmai.com');
--insert into Users values(3,'user2','', 'user2@gmai.com');
--insert into Users values(4,'user3','', 'user3@gmai.com');


/

PRAGMA foreign_keys = on;
/

insert into Project select 1, 'IN_63',1;
insert into Project select 2, 'private',1,1;
/
insert into Project_Member_Status select 1,'guest';
/
insert into Project_Member select 1,1,1; 
insert into Project_Member select 2,1,1; 
insert into Project_Member select 3,1,1; 

/
insert into Project_Member select 1,2,1; 
/

insert into Bug_Severity select 1,'TRIVIAL';
insert into Bug_Severity select 2,'MEDIUM';
insert into Bug_Severity select 3,'MAJOR';
insert into Bug_Severity select 4,'CRITICAL';

/
insert into Bug_Priority select 1,'LOW';
insert into Bug_Priority select 2,'NORMAL';
insert into Bug_Priority select 3,'HIGH';
insert into Bug_Priority select 4,'CRITICAL';
/
insert into Bug_Report_Status select 1,'OPEN';
insert into Bug_Report_Status select 2,'IN PROGRESS';
insert into Bug_Report_Status select 3,'RESOLVED';
insert into Bug_Report_Status select 4,'CLOSED';
insert into Bug_Report_Status select 5,'REOPENED';





