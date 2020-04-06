insert into history select null, user_id, bug_id,'Title',created_at, null,bug_name from bug_report;
insert into history select null, user_id, bug_id,'Summary',created_at, null,bug_summary from bug_report;
insert into history select null, user_id, bug_id,'Status',created_at, null,status_value from bug_report 
join Bug_Report_Status where Bug_Report.bug_report_status_id = Bug_Report_Status.bug_report_status_id ;
insert into history select null, user_id, bug_id,'Severity',created_at, null,severity_value from bug_report
join Bug_Severity where Bug_Report.bug_severity_id = Bug_Severity.bug_severity_id;
insert into history select null, user_id, bug_id,'Priority',created_at, null,priority_value from bug_report 
join Bug_Priority where Bug_Report.bug_priority_id = Bug_Priority.bug_priority_id;
insert into history select null, user_id, bug_id,'Steps to reproduce',created_at, null,steps_to_reproduce from bug_report;
insert into history select null, user_id, bug_id,'Expected result',created_at, null,expected_result from bug_report;
insert into history select null, user_id, bug_id,'Actual result',created_at, null,actual_result from bug_report;