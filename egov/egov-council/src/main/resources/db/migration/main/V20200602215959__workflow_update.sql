ALTER TABLE egcncl_preamble DROP CONSTRAINT fk_egcncl_preambledept;
ALTER TABLE egcncl_preamble ALTER COLUMN department TYPE varchar(30) USING department::varchar;


ALTER TABLE egcncl_router DROP CONSTRAINT fk_eg_department;
ALTER TABLE egcncl_router ALTER COLUMN department TYPE varchar(30) USING department::varchar;

	
update eg_wf_types set link='/services/council/councilpreamble/edit/:ID',displayname='Agenda',lastmodifieddate=now() where type='CouncilPreamble';
INSERT into eg_wf_types (id,"module","type",link,createdby,createddate,lastmodifiedby,lastmodifieddate,enabled,grouped,typefqn,displayname,"version") VALUES 
(nextval('seq_eg_wf_types'),1745,'CouncilMeeting','/services/council/councilmom/new/:ID',1,'2020-04-30 17:58:24.198',1,'2020-05-11 01:55:15.600',true,false,'org.egov.council.entity.CouncilMeeting','MOM',0);

--For Agenda workflow
delete from eg_wf_matrix as ewm where objecttype='CouncilPreamble' and currentstate='NEW' and additionalrule is null and pendingactions='Application Creation';
delete from eg_wf_matrix as ewm where objecttype='CouncilPreamble' and currentstate='Rejected' and additionalrule is null and pendingactions='Application Rejection';
delete from eg_wf_matrix as ewm where objecttype='CouncilPreamble' and currentstate='Created' and additionalrule is null and pendingactions='Commissioner approval pending';
delete from eg_wf_matrix as ewm where objecttype='CouncilPreamble' and currentstate='PreambleCreated' and additionalrule is null;

UPDATE eg_wf_matrix SET department='ANY', objecttype='CouncilPreamble', currentstate='NEW', currentstatus=null, pendingactions=null, currentdesignation='Assistant engineer', additionalrule='CouncilCommonWorkflow', nextstate='Created', nextaction='Agenda branch admin approval pending', nextdesignation='Superintendent', nextstatus='CREATED', validactions='Forward', fromqty=0, toqty=0, fromdate='2020-04-01', todate='2099-04-01', "version"=0, enablefields='', forwardenabled=false, smsemailenabled=false, nextref=0, rejectenabled=false where objecttype='CouncilPreamble' and currentstate='NEW' and additionalrule ='CouncilCommonWorkflow' and nextaction='HOD approval pending';
UPDATE eg_wf_matrix SET department='ANY', objecttype='CouncilPreamble', currentstate='Rejected', currentstatus='REJECTED', pendingactions=null, currentdesignation='Assistant engineer', additionalrule='CouncilCommonWorkflow', nextstate='Created', nextaction='Agenda branch admin approval pending', nextdesignation='Superintendent', nextstatus='CREATED', validactions='Forward,Cancel', fromqty=0, toqty=0, fromdate='2020-04-01', todate='2099-04-01', "version"=0, enablefields='', forwardenabled=false, smsemailenabled=false, nextref=0, rejectenabled=false where objecttype='CouncilPreamble' and currentstate='Rejected' and additionalrule ='CouncilCommonWorkflow' and nextaction='HOD approval pending';
UPDATE eg_wf_matrix SET department='ANY', objecttype='CouncilPreamble', currentstate='Agenda Branch Approved', currentstatus='ABADMINAPPROVED', pendingactions='Secretary approval pending', currentdesignation='Municipal Secretary', additionalrule=null, nextstate='Secretary Approved', nextaction='Commissioner approval pending', nextdesignation='Commissioner', nextstatus='SECRETARYAPPROVED', validactions='Forward,Reject', fromqty=0, toqty=0, fromdate='2020-04-01', todate='2099-04-01', "version"=0, enablefields='', forwardenabled=false, smsemailenabled=false, nextref=0, rejectenabled=false where objecttype='CouncilPreamble' and currentstate='APPROVED' and additionalrule is null and nextaction='Commissioner approval pending';
UPDATE eg_wf_matrix SET department='ANY', objecttype='CouncilPreamble', currentstate='Created', currentstatus='CREATED', pendingactions='Agenda branch admin approval pending', currentdesignation='Superintendent', additionalrule=null, nextstate='Agenda Branch Approved', nextaction='Secretary approval pending', nextdesignation='Municipal Secretary', nextstatus='ABADMINAPPROVED', validactions='Forward,Reject', fromqty=0, toqty=0, fromdate='2020-04-01', todate='2099-04-01', "version"=0, enablefields='', forwardenabled=false, smsemailenabled=false, nextref=0, rejectenabled=false where objecttype='CouncilPreamble' and currentstate='Created' and additionalrule is null and nextaction='Approver approval pending';
UPDATE eg_wf_matrix SET department='ANY', objecttype='CouncilPreamble', currentstate='Secretary Approved', currentstatus='SECRETARYAPPROVED', pendingactions='Commissioner approval pending', currentdesignation='Commissioner', additionalrule=null, nextstate='Commissioner Approved', nextaction='Mayor approval pending', nextdesignation='Mayor', nextstatus='COMMISSIONERAPPROVED', validactions='Forward,Reject', fromqty=0, toqty=0, fromdate='2020-04-01', todate='2099-04-01', "version"=0, enablefields='', forwardenabled=false, smsemailenabled=false, nextref=0, rejectenabled=false where objecttype='CouncilPreamble' and currentstate='APPROVED' and additionalrule is null and nextaction='END';


INSERT INTO eg_wf_matrix (id,department,objecttype,currentstate,currentstatus,pendingactions,currentdesignation,additionalrule,nextstate,nextaction,nextdesignation,nextstatus,validactions,fromqty,toqty,fromdate,todate,"version",enablefields,forwardenabled,smsemailenabled,nextref,rejectenabled) VALUES 
(nextval('seq_eg_wf_matrix'),'ANY','CouncilPreamble','APPROVED','COMMISSIONERAPPROVED','Mayor approval pending','Mayor',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL);

--For MOM workflow
INSERT INTO eg_wf_matrix (id,department,objecttype,currentstate,currentstatus,pendingactions,currentdesignation,additionalrule,nextstate,nextaction,nextdesignation,nextstatus,validactions,fromqty,toqty,fromdate,todate,"version",enablefields,forwardenabled,smsemailenabled,nextref,rejectenabled) VALUES 
(nextval('seq_eg_wf_matrix'),'ANY','CouncilMeeting','NEW',NULL,NULL,'Superintendent','CouncilCommonWorkflow','Created','Secretary approval pending','Municipal Secretary','CREATED','Forward',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','CouncilMeeting','Created','CREATED','Secretary approval pending','Municipal Secretary',NULL,'Secretary Approved','Commissioner approval pending','Commissioner','SECRETARYAPPROVED','Forward,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','CouncilMeeting','Secretary Approved','SECRETARYAPPROVED','Commissioner approval pending','Commissioner',NULL,'Commissioner Approved','Mayor approval pending','Mayor','COMMISSIONERAPPROVED','Forward,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
,(nextval('seq_eg_wf_matrix'),'ANY','CouncilMeeting','Commissioner Approved','COMMISSIONERAPPROVED','Mayor approval pending','Mayor',NULL,'END','END',NULL,'APPROVED','Approve,Reject',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
(nextval('seq_eg_wf_matrix'),'ANY','CouncilMeeting','Rejected','REJECTED',NULL,'Superintendent','CouncilCommonWorkflow','Created','Approval pending','Municipal Secretary','CREATED','Forward',NULL,NULL,'2016-04-01','2099-04-01',0,NULL,NULL,NULL,NULL,NULL)
;


update egw_status set code='ABADMINAPPROVED',description='Agenda Branch Admin Approved' where code='HODAPPROVED';
update egw_status set code='SECRETARYAPPROVED',description='Secretray Approved' where code='MANAGERAPPROVED';
update egw_status set description='APPROVED' where code='APPROVED' and moduletype='COUNCILAGENDA';
update egw_status set description='INWORKFLOW' where code='INWORKFLOW' and moduletype='COUNCILAGENDA';

INSERT INTO egw_status (id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('seq_egw_status'), 'COUNCILPREAMBLE', 'Commissioner Approved', now(), 'COMMISSIONERAPPROVED', 11);
INSERT INTO egw_status (id,moduletype,description,lastmodifieddate,code,order_id) VALUES 
(nextval('seq_egw_status'),'COUNCILAGENDA','AGENDA_STATUS_INWORKFLOW',now(),'INWORKFLOW',2);
INSERT INTO egw_status (id,moduletype,description,lastmodifieddate,code,order_id) VALUES 
(nextval('seq_egw_status'),'CouncilMeeting','CREATED',now(),'CREATED',1)
,(nextval('seq_egw_status'),'CouncilMeeting','APPROVED',now(),'APPROVED',5)
,(nextval('seq_egw_status'),'CouncilMeeting','REJECTED',now(),'REJECTED',6)
,(nextval('seq_egw_status'),'CouncilMeeting','Agenda Branch Admin Approved',now(),'ABADMINAPPROVED',2)
,(nextval('seq_egw_status'),'CouncilMeeting','Secretray Approved',now(),'SECRETARYAPPROVED',3)
(nextval('seq_egw_status'),'CouncilMeeting','Commissioner Approved',now(),'COMMISSIONERAPPROVED',4)
;


update eg_appconfig_values set value='NO' from eg_appconfig where eg_appconfig.id=eg_appconfig_values.key_id and key_name='AGENDA_NUMBER_AUTO';
update eg_appconfig_values set value='NO' from eg_appconfig where eg_appconfig.id=eg_appconfig_values.key_id and key_name='MEETING_NUMBER_AUTO';
update eg_appconfig_values set value='NO' from eg_appconfig where eg_appconfig.id=eg_appconfig_values.key_id and key_name='RESOLUTION_NUMBER_AUTO';
update eg_appconfig_values set value='NO' from eg_appconfig where eg_appconfig.id=eg_appconfig_values.key_id and key_name='PREAMBLE_NUMBER_AUTO';

