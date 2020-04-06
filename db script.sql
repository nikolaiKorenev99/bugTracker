

Drop table "Bug_Report_Status"
/
Drop table "Fixer"
/
Drop table "Project_Member_Status"
/
Drop table "Project_Member"
/
Drop table "Project"
/
Drop table "Users"
/
Drop table "Comments"
/
Drop table "Bug_Priority"
/
Drop table "Bug_Severity"
/
Drop table "Bug_Report"
/
Drop table "Temp_User_History"
/
Drop table "History"
/


-- Create Tables section
 
CREATE TABLE "Users" (
	"user_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"user_name"	Char(20) NOT NULL UNIQUE,
	"password"	Char(20) NOT NULL,
	"email"	Char(50) NOT NULL UNIQUE
)
/
CREATE TABLE "Project" (
	"project_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"project_name"	Char(20) NOT NULL UNIQUE,
	"user_id"	Integer NOT NULL,
	CONSTRAINT "f9" FOREIGN KEY("user_id") REFERENCES "Users"("user_id")
)
/
CREATE TABLE "Project_Member_Status" (
	"status_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"status_value"	TEXT NOT NULL UNIQUE
)
/
CREATE TABLE "Project_Member" (
	"user_id"	Integer NOT NULL,
	"project_id"	Integer NOT NULL,
	"status_id"	Integer NOT NULL,
	CONSTRAINT "f11" FOREIGN KEY("project_id") REFERENCES "Project"("project_id"),
	CONSTRAINT "f12" FOREIGN KEY("status_id") REFERENCES "Project_Member_Status"("status_id"),
	PRIMARY KEY("user_id","project_id"),
	CONSTRAINT "f10" FOREIGN KEY("user_id") REFERENCES "Users"("user_id")
)
/
CREATE TABLE "Bug_Severity" (
	"bug_severity_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"severity_value"	Char(20) NOT NULL UNIQUE
)
/

CREATE TABLE "Bug_Priority" (
	"bug_priority_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"priority_value"	Char(20) NOT NULL UNIQUE
)
/
CREATE TABLE "Bug_Report_Status" (
	"bug_report_status_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"status_value"	Char(20) NOT NULL UNIQUE
)
/
CREATE TABLE "Bug_Report" (
	"bug_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"user_id"	Integer NOT NULL,
	"project_id"	Integer NOT NULL,
	"bug_report_status_id"	Integer NOT NULL,
	"bug_severity_id"	Integer NOT NULL,
	"bug_priority_id"	Integer NOT NULL,
	"bug_name"	Char(100) NOT NULL,
	"bug_summary"	Char(500),
	"created_at"	text NOT NULL,
	"modified_at"	text NOT NULL,
	"steps_to_reproduce"	Char(1000),
	"expected_result"	Char(200),
	"actual_result"	Char(200),
	CONSTRAINT "f2" FOREIGN KEY("bug_priority_id") REFERENCES "Bug_Priority"("bug_priority_id"),
	CONSTRAINT "f1" FOREIGN KEY("bug_severity_id") REFERENCES "Bug_Severity"("bug_severity_id"),
	CONSTRAINT "f4" FOREIGN KEY("project_id") REFERENCES "Project"("project_id"),
	CONSTRAINT "f5" FOREIGN KEY("bug_report_status_id") REFERENCES "Bug_Report_Status"("bug_report_status_id"),
	CONSTRAINT "f3" FOREIGN KEY("user_id") REFERENCES "Users"("user_id")
)
/
CREATE TABLE "Fixer" (
	"user_id"	Integer NOT NULL,
	"bug_id"	Integer NOT NULL,
	CONSTRAINT "f13" FOREIGN KEY("bug_id") REFERENCES "Bug_Report"("bug_id"),
	CONSTRAINT "f14" FOREIGN KEY("user_id") REFERENCES "Users"("user_id"),
	PRIMARY KEY("user_id","bug_id")
)
/
CREATE TABLE "Comments" (
	"comment_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"comment_value"	Char(500),
	"user_id"	Integer NOT NULL,
	"bug_id"	Integer NOT NULL,
	"created_at"	text NOT NULL,
	CONSTRAINT "f7" FOREIGN KEY("bug_id") REFERENCES "Bug_Report"("bug_id"),
	CONSTRAINT "f8" FOREIGN KEY("user_id") REFERENCES "Users"("user_id")
)
/

CREATE TABLE "Temp_User_History"(
	"user_id" Integer NOT NULL,
 	CONSTRAINT "f9" FOREIGN KEY("user_id") REFERENCES "Users"("user_id")
 );
 /
 
CREATE TABLE "History"(
	"history_id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"user_id"	Integer NOT NULL,
	"bug_id"	Integer NOT NULL, 
	"field_name" Char(50) NOT NULL,
	"modified_at"	text NOT NULL,
	"old_value"		Char(1000),  -- operation create ticket old value is null
	"new_value"     Char(1000) NOT NULL,
	CONSTRAINT "f10" FOREIGN KEY("user_id") REFERENCES "Users"("user_id"),
	CONSTRAINT "f11" FOREIGN KEY("bug_id") REFERENCES "Bug_Report"("bug_id")
 );
 /
 
PRAGMA foreign_keys = on;
/


CREATE TRIGGER "Insert_History_Trigger"
AFTER INSERT ON Bug_Report
BEGIN
  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Title", NEW.modified_at, NULL, NEW.bug_name);

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Summary", NEW.modified_at, NULL, NEW.bug_summary);

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Status", NEW.modified_at, NULL, (SELECT status_value FROM Bug_Report_Status WHERE bug_report_status_id = NEW.bug_report_status_id));

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Severity", NEW.modified_at, NULL, (SELECT severity_value FROM Bug_Severity WHERE bug_severity_id = NEW.bug_severity_id));

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Priority", NEW.modified_at, NULL, (SELECT priority_value FROM Bug_Priority WHERE bug_priority_id = NEW.bug_priority_id));

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Steps to reproduce", NEW.modified_at, NULL, NEW.steps_to_reproduce);

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Expected result", NEW.modified_at, NULL, NEW.expected_result);

  INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) VALUES (NEW.user_id, NEW.bug_id, "Actual result", NEW.modified_at, NULL, NEW.actual_result);
END;
/


CREATE TRIGGER Update_History_Title_Trigger
AFTER UPDATE ON Bug_Report
WHEN 
NEW.bug_name != OLD.bug_name
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Title", NEW.modified_at, OLD.bug_name, NEW.bug_name);
END;

/
CREATE TRIGGER Update_History_Summary_Trigger
AFTER UPDATE ON Bug_Report
WHEN
NEW.bug_summary != OLD.bug_summary
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Summary", NEW.modified_at, OLD.bug_summary , NEW.bug_summary); 
END;
/
CREATE TRIGGER Update_History_Status_Trigger
AFTER UPDATE ON Bug_Report
WHEN
(SELECT status_value FROM Bug_Report_Status WHERE bug_report_status_id = NEW.bug_report_status_id) != (SELECT status_value FROM Bug_Report_Status WHERE bug_report_status_id = OLD.bug_report_status_id)
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Status", NEW.modified_at, (SELECT status_value FROM Bug_Report_Status WHERE bug_report_status_id = OLD.bug_report_status_id) , (SELECT status_value FROM Bug_Report_Status WHERE bug_report_status_id = NEW.bug_report_status_id));
END;
/
CREATE TRIGGER Update_History_Severity_Trigger
AFTER UPDATE ON Bug_Report
WHEN
(SELECT severity_value FROM Bug_Severity WHERE bug_severity_id = NEW.bug_severity_id) != (SELECT severity_value FROM Bug_Severity WHERE bug_severity_id = OLD.bug_severity_id)
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Severity", NEW.modified_at, (SELECT severity_value FROM Bug_Severity WHERE bug_severity_id = OLD.bug_severity_id) , (SELECT severity_value FROM Bug_Severity WHERE bug_severity_id = NEW.bug_severity_id));
END;
/
CREATE TRIGGER Update_History_Priority_Trigger
AFTER UPDATE ON Bug_Report
WHEN
(SELECT priority_value FROM Bug_Priority WHERE bug_priority_id = NEW.bug_priority_id) != (SELECT priority_value FROM Bug_Priority WHERE bug_priority_id = OLD.bug_priority_id)
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Priority", NEW.modified_at, (SELECT priority_value FROM Bug_Priority WHERE bug_priority_id = OLD.bug_priority_id) , (SELECT priority_value FROM Bug_Priority WHERE bug_priority_id = NEW.bug_priority_id));
END;
/
CREATE TRIGGER Update_History_Steps_Trigger
AFTER UPDATE ON Bug_Report
WHEN
NEW.steps_to_reproduce != OLD.steps_to_reproduce
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Steps to reproduce", NEW.modified_at, OLD.steps_to_reproduce , NEW.steps_to_reproduce);
END;
/
CREATE TRIGGER Update_History_ER_Trigger
AFTER UPDATE ON Bug_Report
WHEN
NEW.expected_result != OLD.expected_result
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Expected result", NEW.modified_at, OLD.expected_result , NEW.expected_result);
END;
/
CREATE TRIGGER Update_History_AR_Trigger
AFTER UPDATE ON Bug_Report
WHEN
NEW.actual_result != OLD.actual_result
BEGIN
INSERT INTO History (user_id, bug_id, field_name, modified_at, old_value, new_value) 
VALUES 
((SELECT user_id FROM Temp_User_History), NEW.bug_id, "Actual result", NEW.modified_at, OLD.actual_result , NEW.actual_result);
END;
/
alter table Project add "is_private" Integer NOT NULL DEFAULT 0;
/

PRAGMA foreign_keys = on;
PRAGMA primary_keys = on;
/

alter table users add "salt" Char(50) NOT NULL default 0;
PRAGMA foreign_keys = on;
PRAGMA primary_keys = on;