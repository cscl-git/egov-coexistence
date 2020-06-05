INSERT INTO eg_module (id,"name",enabled,contextroot,parentmodule,displayname,ordernumber,rootmodule) VALUES 
(nextval('seq_eg_module'),'Apnimandi',false,'apnimandi',NULL,'Apnimandi',10,NULL);

INSERT INTO egw_status (id,moduletype,description,lastmodifieddate,code,order_id) VALUES 
(nextval('seq_egw_status'),'ApnimandiContractor','Created',now(),'SDCCREATED',1)
,(nextval('seq_egw_status'),'ApnimandiContractor','Approved By Junior Engineer',now(),'JECONTRACTORAPPROVED',2)
,(nextval('seq_egw_status'),'ApnimandiContractor','Rejected',now(),'REJECTED',3)
,(nextval('seq_egw_status'),'ApnimandiContractor','Approved By Superintendent',now(),'APPROVED',4)
,(nextval('seq_egw_status'),'ApnimandiContractor','Contract Terminated',now(),'CONTRACTTERMINATED',5)
,(nextval('seq_egw_status'),'ApnimandiContractor','Deleted',now(),'DELETED',6)
,(nextval('seq_egw_status'),'ApnimandiContractor','Resubmitted',now(),'RESUBMITTED',7)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Created',now(),'AMCCREATED',1)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Approved By Sub Divisional Engineer',now(),'APPROVED',2)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Rejected',now(),'REJECTED',3)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Deleted',now(),'DELETED',4)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Resubmitted',now(),'RESUBMITTED',5);

INSERT INTO eg_wf_matrix (id,department,objecttype,currentstate,currentstatus,pendingactions,currentdesignation,additionalrule,nextstate,nextaction,nextdesignation,nextstatus,validactions,fromqty,toqty,fromdate,todate,"version",enablefields,forwardenabled,smsemailenabled,nextref,rejectenabled) VALUES 
(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiContractor','NEW',NULL,NULL,'Sub Divisional Clerk',NULL,'Created','Junior Engineer approval pending','Junior Engineer','SDCCREATED','Forward',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiContractor','Created','SDCCREATED','Junior Engineer approval pending','Junior Engineer',NULL,'Approved By Junior Engineer','Superintendent approval pending','Superintendent','JECONTRACTORAPPROVED','Forward,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiContractor','Approved By Junior Engineer','JECONTRACTORAPPROVED','Superintendent approval pending','Superintendent',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiContractor','Rejected','REJECTED',NULL,'Sub Divisional Clerk',NULL,'Resubmitted','Junior Engineer approval pending','Junior Engineer','RESUBMITTED','Forward,Delete',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiContractor','Resubmitted','RESUBMITTED','Junior Engineer approval pending','Junior Engineer',NULL,'Approved By Junior Engineer','Superintendent approval pending','Superintendent','JECONTRACTORAPPROVED','Forward,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','NEW',NULL,NULL,'Sub Divisional Clerk',NULL,'Created','Sub Divisional Engineer approval pending','Sub Divisional Engineer','AMCCREATED','Forward',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','Created','AMCCREATED','Sub Divisional Engineer approval pending','Sub Divisional Engineer',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','Rejected','REJECTED',NULL,'Sub Divisional Clerk',NULL,'Resubmitted','Sub Divisional Engineer approval pending','Sub Divisional Engineer','RESUBMITTED','Forward,Delete',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','Resubmitted','RESUBMITTED','Sub Divisional Engineer approval pending','Sub Divisional Engineer',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL);

INSERT INTO eg_wf_dept_desg_map (id,objecttype,currentstate,nextdepartment,nextdesignation,additionalrule,"version") VALUES 
(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Rejected','999','MS01',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Rejected','999','MS01',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','NEW','999','MS01',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Created','999','999',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Approved By Junior Engineer','','',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','NEW','999','MS01',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Created','','',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Resubmitted','999','MS01',NULL,0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Resubmitted',NULL,NULL,NULL,0);

INSERT INTO eg_wf_types (id,"module","type",link,createdby,createddate,lastmodifiedby,lastmodifieddate,enabled,grouped,typefqn,displayname,"version") VALUES 
(nextval('seq_eg_wf_types'),(select id from eg_module where "name"='Apnimandi'),'ApnimandiContractor','/services/apnimandi/contractor/workflow/view/:ID',1,now(),1,now(),true,false,'org.egov.apnimandi.transactions.entity.ApnimandiContractor','Contractor',0)
,(nextval('seq_eg_wf_types'),(select id from eg_module where "name"='Apnimandi'),'ApnimandiCollectionDetails','/services/apnimandi/collection/workflow/view/:ID',1,now(),1,now(),true,false,'org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails','Collection',0);


