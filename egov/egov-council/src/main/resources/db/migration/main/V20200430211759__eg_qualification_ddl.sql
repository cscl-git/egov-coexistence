CREATE TABLE eg_qualification (
id int8 NOT NULL,
"name" varchar(100) NOT NULL,
isactive bool NOT NULL DEFAULT true,
createddate timestamp NULL,
createdby int8 NULL,
lastmodifieddate timestamp NULL,
lastmodifiedby int8 NULL,
"version" int8 NULL,
code varchar(20) NOT NULL,
description varchar(100) NULL,
CONSTRAINT pk_eg_qualification PRIMARY KEY (id)
);

INSERT INTO eg_qualification (id,"name",isactive,createddate,createdby,lastmodifieddate,lastmodifiedby,"version",code,description) VALUES
(1,'Less Than SSC',true,'2020-04-08 02:24:40.665',1,'2020-04-08 02:24:40.665',1,0,'LSSC','Less Than SSC')
,(2,'SSC',true,'2020-04-08 02:24:40.665',1,'2020-04-08 02:24:40.665',1,0,'SSC','SSC')
,(3,'Intermediate',true,'2020-04-08 02:24:40.665',1,'2020-04-08 02:24:40.665',1,0,'Intermediate','Intermediate')
,(4,'Graduation',true,'2020-04-08 02:24:40.665',1,'2020-04-08 02:24:40.665',1,0,'Graduation','Graduation')
,(5,'Post-Graduation',true,'2020-04-08 02:24:40.665',1,'2020-04-08 02:24:40.665',1,0,'Post-Graduation','Post-Graduation')
,(6,'PHD',true,'2020-04-08 02:24:40.665',1,'2020-04-08 02:24:40.665',1,0,'PHD','PHD')
,(7,'Others',true,'2020-04-08 02:25:17.104',1,'2020-04-08 02:25:17.104',1,0,'Others','Others')
;