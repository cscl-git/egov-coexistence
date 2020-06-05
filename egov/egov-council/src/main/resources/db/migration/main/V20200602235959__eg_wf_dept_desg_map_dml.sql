INSERT INTO eg_wf_dept_desg_map (id,objecttype,currentstate,nextdepartment,nextdesignation,additionalrule,"version") VALUES 
(6,'CouncilPreamble','Commissioner Approved','999',NULL,NULL,0)
,(1,'CouncilPreamble','NEW','391','7','CouncilCommonWorkflow',0)
,(2,'CouncilPreamble','Rejected','391','7','CouncilCommonWorkflow',0)
,(3,'CouncilPreamble','Agenda Branch Approved','999','214',NULL,0)
,(4,'CouncilPreamble','Created','999','MS01',NULL,0)
,(5,'CouncilPreamble','Secretary Approved','999','999',NULL,0)
,(15,'CouncilMeeting','Secretary Approved','999','999',NULL,0)
,(16,'CouncilMeeting','Commissioner Approved','999',NULL,NULL,0)
,(12,'CouncilMeeting','NEW','999','MS01','CouncilCommonWorkflow',0)
,(13,'CouncilMeeting','Rejected','999','MS01','CouncilCommonWorkflow',0)
;
INSERT INTO eg_wf_dept_desg_map (id,objecttype,currentstate,nextdepartment,nextdesignation,additionalrule,"version") VALUES 
(14,'CouncilMeeting','Created','999','214',NULL,0)
;