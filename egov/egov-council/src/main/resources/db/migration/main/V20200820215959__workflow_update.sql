INSERT INTO eg_wf_matrix
(id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate, "version", enablefields, forwardenabled, smsemailenabled, nextref, rejectenabled)
VALUES(nextval('seq_eg_wf_matrix'), 'ANY', 'CouncilPreamble', 'NEW', NULL, NULL, 'Agenda Branch Admin', 'CouncilABAWorkflow', 'Agenda Branch Approved', 'Secretary approval pending', 'Municipal Secretary', 'ABADMINAPPROVED', 'Forward', NULL, NULL, '2016-04-01', '2099-04-01', 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO eg_wf_matrix
(id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate, "version", enablefields, forwardenabled, smsemailenabled, nextref, rejectenabled)
VALUES(nextval('seq_eg_wf_matrix'), 'ANY', 'CouncilPreamble', 'Rejected', 'REJECTED', NULL, 'Agenda Branch Admin', 'CouncilABAWorkflow', 'Agenda Branch Approved', 'Secretary approval pending', 'Municipal Secretary', 'ABADMINAPPROVED', 'Forward,Cancel', NULL, NULL, '2016-04-01', '2099-04-01', 0, NULL, NULL, NULL, NULL, NULL);

INSERT INTO eg_wf_dept_desg_map (id,objecttype,currentstate,nextdepartment,nextdesignation,additionalrule,"version") VALUES 
(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','NEW','999','MS01','CouncilABAWorkflow',0)
,(nextval('seq_eg_wf_dept_desg_map'),'CouncilPreamble','Rejected','999','MS01','CouncilABAWorkflow',0);