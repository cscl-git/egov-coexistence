delete from eglc_petitiontype_master;
delete from eglc_court_master;
delete from eglc_casetype_master;
delete from eglc_courttype_master;
delete from eglc_judgmenttype_master;

INSERT INTO eglc_casetype_master (id,code,casetype,notes,ordernumber,active,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES 
(2,'PUBHEALTH','Public Health','Public Health',2,true,'2020-04-29 22:35:07.637','2020-04-29 22:35:07.637',1,1,0)
,(1,'ADMIN','Administration','Administration',1,true,'2020-04-29 22:32:46.277','2020-04-29 22:38:12.365',1,1,1)
,(3,'ELECTIONS','Elections','Elections',3,true,'2020-04-29 22:38:34.241','2020-04-29 22:38:34.241',1,1,0)
,(4,'SERVICEMATTERS','Service matters','Service matters',4,true,'2020-04-30 15:48:24.735','2020-04-30 15:48:24.735',1,1,0)
,(5,'TOWNPLAN','Town Planning','Town Planning',5,true,'2020-04-30 15:55:34.540','2020-04-30 15:55:34.540',1,1,0)
,(6,'LANDREFORMS','Land Reforms','Land Reforms',6,true,'2020-04-30 15:56:20.560','2020-04-30 15:56:20.560',1,1,0)
,(7,'ENGG','Engineering','Engineering',7,true,'2020-05-05 14:30:20.855','2020-05-05 14:30:20.855',1,1,0)
,(8,'REVENUE','Revenue','Revenue',8,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(9,'OTHERS','Others','Others',9,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(10,'ENFORCEMENT','Enforcement ','Enforcement ',10,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(11,'LANDACQUISITION','land acquisition ','land acquisition ',11,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(12,'PROPERTYMATTERS','property matters','property matters',12,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(13,'CONTRACTAGREEMENT','contract agreement','contract agreement',13,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(14,'AUCTION','Auction ','Auction ',14,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(15,'FIREMATTERS','Fire matters','Fire matters',15,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(16,'PROPERTYTAX','property tax ','property tax',16,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(17,'ADVERTISEMENTMATTER','Advertisement matter','Advertisement matter',17,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(18,'PAGRCOMMUNCENBOOKCASES','Park/ground/community centre booking cases','Park/ground/community centre booking cases',18,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(19,'STREETVENDORMATTERS','Street vendor matters','Street vendor matters',19,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(20,'REHABOFCOLSMATTERS','rehabilitation of colonies matters ','rehabilitation of colonies matters ',20,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(21,'POLICIESANDSCHMATTERS','Policies matters','Policies matters',21,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(22,'SOLIDWASTEMANAGERULES','Solid waste management rules ','Solid waste management rules ',22,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
,(23,'SANITATINMATTERS','Sanitatin matters','Sanitatin matters',23,true,'2020-06-02 14:46:09.413','2020-06-02 14:46:09.413',1,1,0)
;

INSERT INTO eglc_courttype_master (id,code,courttype,notes,ordernumber,active,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES 
(1,'HIGHCOURT','High Court','High Court',1,true,'2020-04-29 22:43:29.037','2020-04-29 22:43:29.037',1,1,0)
,(2,'SUPCOURT','Supreme Court','Supreme Court',2,true,'2020-04-29 22:44:40.024','2020-04-29 22:44:40.024',1,1,0)
,(5,'DISTRICTCOURT','District Court','District Court',5,true,'2020-06-02 14:15:42.445','2020-06-02 14:15:42.445',1,1,0)
,(8,'LABOURCOURT','Labour Court','Labour Court',8,true,'2020-06-02 14:15:42.445','2020-06-02 14:15:42.445',1,1,0)
,(3,'PLA','Permanent Lok Adalat','Permanent Lok Adalat',3,true,'2020-04-29 23:39:48.009','2020-04-29 23:39:48.009',1,1,0)
,(6,'CAC','Chief Administrator court','Chief Administrator court',6,true,'2020-06-02 14:15:42.445','2020-06-02 14:15:42.445',1,1,0)
,(7,'ATAC','Advisor to Administrator court','Advisor to Administrator court',7,true,'2020-06-02 14:15:42.445','2020-06-02 14:15:42.445',1,1,0)
,(4,'NGTND','National Green Tribunal New Delhi','National Green Tribunal New Delhi',4,true,'2020-05-12 17:29:06.179','2020-05-12 17:29:06.179',1,1,0)
,(9,'NDRC','National Consumer Disputes Redressal Commission','National Consumer Disputes Redressal Commission',9,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(10,'NCOI','National Commission of India','National Commission of India',10,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(11,'NHRCD','National Human Right Commission Dehli','National Human Right Commission Dehli',11,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(12,'CATCC','Central Administrative Tribunal court Chandigarh','Central Administrative Tribunal court Chandigarh',12,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(13,'SCCC','State consumer Court Chandigarh','State consumer Court Chandigarh',13,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(14,'DCCC','District Consumer court Chandigarh','District Consumer court Chandigarh',14,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(15,'PFCC','Provident fund court Chandigarh','Provident fund court Chandigarh',15,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(16,'DECCC','Deputy Commissioner court Chandigarh','Deputy Commissioner court Chandigarh',16,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
,(17,'ALCCC','Assistant Labour Commissioner court Chandigarh','Assistant Labour Commissioner court Chandigarh',17,true,'2020-06-08 20:37:38.998','2020-06-08 20:37:38.998',1,1,0)
;

INSERT INTO eglc_judgmenttype_master (id,code,judgmenttype,description,ordernumber,active,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES 
(1,'ABATES','Abates','Abates',1,true,'2020-04-30 12:57:22.762','2020-04-30 12:57:22.762',1,1,0)
,(2,'AGAINST','Against','Against',2,true,'2020-04-30 14:27:11.448','2020-04-30 14:27:11.448',1,1,0)
,(3,'ARBITRATION','Arbitration','Arbitration',3,true,'2020-04-30 14:27:29.543','2020-04-30 14:27:29.543',1,1,0)
,(4,'DISMISSAL','Dismissal','Dismissal',6,true,'2020-04-30 14:30:09.112','2020-04-30 14:30:09.112',1,1,0)
,(5,'ENQUIRY','Enquiry','Enquiry',7,true,'2020-04-30 14:31:23.046','2020-04-30 14:31:23.046',1,1,0)
,(6,'ALLOWED','Allowed','Allowed',4,true,'2020-04-30 14:33:24.121','2020-04-30 14:33:24.121',1,1,0)
,(7,'CLOSED','Closed','Closed',5,true,'2020-04-30 14:33:46.967','2020-04-30 14:33:46.967',1,1,0)
,(8,'EXPORDERE','Ex-Parte Order','Ex-Parte Order',8,true,'2020-04-30 14:34:13.044','2020-04-30 14:34:13.044',1,1,0)
,(9,'WITHDRAWN','Withdrawn','Withdrawn',9,true,'2020-04-30 14:34:40.800','2020-04-30 14:34:40.800',1,1,0)
,(10,'DECIDED','Decided','Decided',10,true,'2020-06-02 15:05:28.129','2020-06-02 15:05:28.129',1,1,0)
;

INSERT INTO eglc_court_master (id,name,address,courttype,ordernumber,active,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES 
(4,'District Court','District Court',5,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(5,'Labour Court ','Labour Court ',8,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(6,'Permanent Lok Adalat ','Permanent Lok Adalat ',3,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(7,'Chief Administrator court ','Chief Administrator court ',6,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(8,'Advisor to Administrator court ','Advisor to Administrator court ',7,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(9,'National Green Tribunal New Delhi','National Green Tribunal New Delhi',4,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(10,'National Consumer Disputes Redressal Commission','National Consumer Disputes Redressal Commission',9,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(11,'National Commission of India ','National Commission of India ',10,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(12,'National Human Right Commission Dehli','National Human Right Commission Dehli',11,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(13,'Central Administrative Tribunal court Chandigarh ','Central Administrative Tribunal court Chandigarh ',12,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(14,'State consumer Court Chandigarh','State consumer Court Chandigarh',13,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(15,'District Consumer court Chandigarh','District Consumer court Chandigarh',14,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(16,'Provident fund court Chandigarh ','Provident fund court Chandigarh ',15,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(17,'Deputy Commissioner court Chandigarh ','Deputy Commissioner court Chandigarh ',16,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(18,'Assistant Labour Commissioner court Chandigarh','Assistant Labour Commissioner court Chandigarh',17,1,true,'2020-06-08 21:23:41.059','2020-06-08 21:23:41.059',1,1,0)
,(1,'High court Punjab and Haryana','High court Punjab and Haryana',1,1,true,'2020-04-30 14:25:16.452','2020-06-08 22:47:50.187',1,1,1)
,(2,'Supreme Court of India','Supreme Court of India',2,2,true,'2020-04-30 14:25:37.781','2020-06-08 22:48:28.851',1,1,1)
;

INSERT INTO eglc_petitiontype_master (id,code,petitiontype,courttype,ordernumber,active,createddate,lastmodifieddate,createdby,lastmodifiedby,"version") VALUES 
(1,'WPC','WRIT PETITION CIVIL',1,1,true,'2020-04-30 00:35:10.990','2020-04-30 00:35:10.990',1,1,0)
,(2,'CWP','CWP',1,2,true,'2020-04-30 00:46:01.495','2020-04-30 00:46:01.495',1,1,0)
,(3,'RSA','RSA',1,3,true,'2020-04-30 00:58:26.977','2020-04-30 00:58:26.977',1,1,0)
,(5,'LPA','LPA',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(6,'CRM','CRM',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(7,'COCP','COCP',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(8,'CRR','CRR',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(9,'CA','CA',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(10,'FAO','FAO',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(11,'IOIN','IOIN',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(12,'CRWP','CRWP',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(13,'CPMISC','CP-MISC',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(14,'ARB','ARB',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(15,'CIVILSUIT','Civil Suit',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(16,'ADJ','ADJ',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(17,'APPAPPEAL','APPLICATION/APPEAL',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(18,'APPEALCOMPLAINTCASE','APPEAL COMPLAINT/CASE',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
,(19,'ORIGINALAPPCONTEMPT','ORIGINAL APPLICATION AND CONTEMPT',1,1,true,'2020-06-08 21:53:03.053','2020-06-08 21:53:03.053',1,1,0)
;

SELECT setval('seq_eglc_petitiontype_master', (SELECT max(id) FROM eglc_petitiontype_master));
SELECT setval('seq_eglc_casetype_master', (SELECT max(id) FROM eglc_casetype_master));
SELECT setval('seq_eglc_courttype_master', (SELECT max(id) FROM eglc_courttype_master));
SELECT setval('seq_eglc_judgmenttype_master', (SELECT max(id) FROM eglc_judgmenttype_master));
SELECT setval('seq_eglc_court_master', (SELECT max(id) FROM eglc_court_master));