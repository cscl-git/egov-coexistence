INSERT INTO eg_module (id,"name",enabled,contextroot,parentmodule,displayname,ordernumber,rootmodule) VALUES 
(nextval('seq_eg_module'),'Apnimandi',false,'apnimandi',NULL,'Apnimandi',10,NULL);

INSERT INTO egw_status (id,moduletype,description,lastmodifieddate,code,order_id) VALUES 
(nextval('seq_egw_status'),'ApnimandiContractor','Created','2020-05-12 21:35:59.139','SDCCREATED',1)
,(nextval('seq_egw_status'),'ApnimandiContractor','Approved By Junior Engineer','2020-05-12 21:35:59.139','JECONTRACTORAPPROVED',2)
,(nextval('seq_egw_status'),'ApnimandiContractor','Rejected','2020-05-12 21:35:59.139','REJECTED',3)
,(nextval('seq_egw_status'),'ApnimandiContractor','Approved','2020-05-12 21:35:59.139','APPROVED',4)
,(nextval('seq_egw_status'),'ApnimandiContractor','Contract Terminated','2020-05-12 21:35:59.139','CONTRACTTERMINATED',5)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Created','2020-05-23 17:48:18.364','AMCCREATED',1)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Approved','2020-05-23 17:48:18.364','APPROVED',2)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Rejected','2020-05-23 17:48:18.364','REJECTED',3)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Deleted','2020-05-23 17:48:18.364','DELETED',4)
,(nextval('seq_egw_status'),'ApnimandiContractor','Deleted','2020-05-28 20:32:54.008','DELETED',6)
,(nextval('seq_egw_status'),'ApnimandiContractor','Resubmitted','2020-05-28 21:01:38.279','RESUBMITTED',7)
,(nextval('seq_egw_status'),'ApnimandiCollectionDetails','Resubmitted','2020-05-29 17:34:43.153','RESUBMITTED',5)
;

INSERT INTO eg_wf_matrix (id,department,objecttype,currentstate,currentstatus,pendingactions,currentdesignation,additionalrule,nextstate,nextaction,nextdesignation,nextstatus,validactions,fromqty,toqty,fromdate,todate,"version",enablefields,forwardenabled,smsemailenabled,nextref,rejectenabled) VALUES 
(nextval('eg_wf_matrix'),'ANY','ApnimandiContractor','NEW',NULL,NULL,'Sub Divisional Engineer',NULL,'Created','Junior Engineer approval pending','Junior Engineer','SDCCREATED','Forward',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiContractor','Created','SDCCREATED','Junior Engineer approval pending','Junior Engineer',NULL,'END','END','','APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiContractor','Rejected','REJECTED',NULL,'Sub Divisional Engineer',NULL,'Resubmitted','Junior Engineer approval pending','Junior Engineer','RESUBMITTED','Forward,Delete',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','NEW',NULL,NULL,'Sub Divisional Clerk',NULL,'Created','Sub Divisional Engineer approval pending','Sub Divisional Engineer','AMCCREATED','Forward',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','Created','AMCCREATED','Sub Divisional Engineer approval pending','Sub Divisional Engineer',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','Rejected','REJECTED',NULL,'Sub Divisional Clerk',NULL,'Resubmitted','Sub Divisional Engineer approval pending','Sub Divisional Engineer','RESUBMITTED','Forward,Delete',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiContractor','Resubmitted','RESUBMITTED','Junior Engineer approval pending','Junior Engineer',NULL,'END','END','','APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('eg_wf_matrix'),'ANY','ApnimandiCollectionDetails','Resubmitted','RESUBMITTED','Sub Divisional Engineer approval pending','Sub Divisional Engineer',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
;

create sequence seq_eg_wf_dept_desg_map;

INSERT INTO eg_wf_dept_desg_map (id,objecttype,currentstate,nextdepartment,nextdesignation,additionalrule,"version") VALUES 
(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','NEW','427','100','RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Created','','','RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Resubmitted','','','RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Rejected','427','100','RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','NEW','428','100','RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Created','','','RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Resubmitted','','','RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Rejected','428','100','RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','NEW','429','100','RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Created','','','RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Resubmitted','','','RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiContractor','Rejected','429','100','RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','NEW','427','227','RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Created',NULL,NULL,'RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Rejected','427','227','RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Resubmitted',NULL,NULL,'RD1',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','NEW','428','227','RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Created',NULL,NULL,'RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Rejected','428','227','RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Resubmitted',NULL,NULL,'RD2',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','NEW','429','227','RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Created',NULL,NULL,'RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Rejected','429','227','RD3',0)
,(nextval('seq_eg_wf_dept_desg_map'),'ApnimandiCollectionDetails','Resubmitted',NULL,NULL,'RD3',0)
;

INSERT INTO eg_wf_types (id,"module","type",link,createdby,createddate,lastmodifiedby,lastmodifieddate,enabled,grouped,typefqn,displayname,"version") VALUES 
(nextval('seq_eg_wf_types'),(select id from eg_module where "name"='Apnimandi'),'ApnimandiContractor','/services/apnimandi/contractor/workflow/view/:ID',1,now(),1,now(),true,false,'org.egov.apnimandi.transactions.entity.ApnimandiContractor','Contractor',0)
,(nextval('seq_eg_wf_types'),(select id from eg_module where "name"='Apnimandi'),'ApnimandiCollectionDetails','/services/apnimandi/collection/workflow/view/:ID',1,now(),1,now(),true,false,'org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails','Collection',0);


