INSERT INTO eg_wf_dept_desg_map (id,objecttype,currentstate,nextdepartment,nextdesignation,additionalrule,"version") VALUES 
(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','Commissioner Approved','999',NULL,NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','NEW','391','7','CouncilCommonWorkflow',0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','Rejected','391','7','CouncilCommonWorkflow',0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','Agenda Branch Approved','999','214',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','Created','999','MS01',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','Secretary Approved','999','999',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilMeeting','Secretary Approved','999','999',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilMeeting','Commissioner Approved','999',NULL,NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilMeeting','NEW','999','MS01','CouncilCommonWorkflow',0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilMeeting','Rejected','999','MS01','CouncilCommonWorkflow',0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilMeeting','Created','999','214',NULL,0);