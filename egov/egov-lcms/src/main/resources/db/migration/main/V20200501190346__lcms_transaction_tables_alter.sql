ALTER TABLE eglc_judgment ADD COLUMN "isdepartmentacted" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_judgment ADD COLUMN "departmentactiondetails" VARCHAR(1024);

ALTER TABLE eglc_judgment_aud ADD COLUMN "isdepartmentacted" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_judgment_aud ADD COLUMN "departmentactiondetails" VARCHAR(1024);

ALTER TABLE eglc_employeehearing ADD COLUMN "employeename" VARCHAR(200);
ALTER TABLE eglc_employeehearing_aud ADD COLUMN "employeename" VARCHAR(200);

ALTER TABLE eglc_employeehearing RENAME COLUMN "employee" TO "employeeid";
ALTER TABLE eglc_employeehearing_aud RENAME COLUMN "employee" TO "employeeid";

ALTER TABLE eglc_employeehearing DROP CONSTRAINT fk_employeehearing_employee;

ALTER TABLE eglc_bipartisandetails ALTER COLUMN "name" TYPE VARCHAR (250);
ALTER TABLE eglc_bipartisandetails_aud ALTER COLUMN "name" TYPE VARCHAR (250);

ALTER TABLE eglc_employeehearing ADD COLUMN "department" VARCHAR(200);
ALTER TABLE eglc_employeehearing_aud ADD COLUMN "department" VARCHAR(200);

ALTER TABLE eglc_employeehearing ADD COLUMN "designation" VARCHAR(200);
ALTER TABLE eglc_employeehearing_aud ADD COLUMN "designation" VARCHAR(200);

ALTER TABLE eglc_employeehearing ADD COLUMN "contactno" VARCHAR(200);
ALTER TABLE eglc_employeehearing_aud ADD COLUMN "contactno" VARCHAR(200);

ALTER TABLE eglc_employeehearing DROP COLUMN "employeeid";

create sequence "seq_eglc_concerned_branch_master";

CREATE TABLE eglc_concerned_branch_master (
	id int8 NOT NULL,
	concernedbranch varchar(50) NOT NULL,
	notes varchar(256) NULL,
	ordernumber numeric NULL,
	active bool NOT NULL,
	createddate timestamp NOT NULL,
	lastmodifieddate timestamp NOT NULL,
	createdby int8 NOT NULL,
	lastmodifiedby int8 NOT NULL,
	"version" numeric NULL DEFAULT 0,
	CONSTRAINT pk_concerned_branch_master PRIMARY KEY (id)
);

INSERT INTO eglc_concerned_branch_master(id, concernedbranch, notes, ordernumber, active, createddate, lastmodifieddate, createdby, lastmodifiedby, "version")VALUES
(nextval('seq_eglc_concerned_branch_master'), 'The Superintending Engineer, Public Health, MCC','The Superintending Engineer, Public Health, MCC',1, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Superintending Engineer, B & R, MCC','The Superintending Engineer, B & R, MCC',2, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Superintending Engineer, H & E, MCC','The Superintending Engineer, H & E, MCC',3, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Medical Officer of Health, MCC','The Medical Officer of Health, MCC',4, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Chief Accounts Officer, MCC','The Chief Accounts Officer, MCC',5, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The SDE (B), MCC','The SDE (B), MCC',6, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The SDE (HQ), Parking Branch, MCC','The SDE (HQ), Parking Branch, MCC',7, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Sub-Office, Manimajra, MCC','Sub-Office, Manimajra, MCC',8, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Establishment Branch, MCC','Establishment Branch, MCC',9, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Colony Branch, MCC','Colony Branch, MCC',10, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Licensing Branch, MCC','Licensing Branch, MCC',11, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Enforcement Branch, MCC','Enforcement Branch, MCC',12, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Fire Branch, MCC','Fire Branch, MCC',13, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Tax Branch, MCC','Tax Branch, MCC',14, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Assistant Controller (F&A), Estate Branch, MCC','The Assistant Controller (F&A), Estate Branch, MCC',15, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Section Officer, Estate Branch/ ACF & A, MCC','The Section Officer, Estate Branch/ ACF & A, MCC',16, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The OSD-II/ Booking Branch, MCC','The OSD-II/ Booking Branch, MCC',17, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'The Senior Assistant, Agenda Branch, MCC','The Senior Assistant, Agenda Branch, MCC',18, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Vendor Cell','Vendor Cell',19, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'Day NULM','Day NULM',20, true, now(), now(), 1, 1, 0)
,(nextval('seq_eglc_concerned_branch_master'), 'House Allotment','House Allotment',21, true, now(), now(), 1, 1, 0)
;

alter table eglc_legalcase add column concernedbranch bigint;
alter table eglc_legalcase add column concernedbranchemail varchar(100);

alter table eglc_legalcase_aud add column concernedbranch bigint;
alter table eglc_legalcase_aud add column concernedbranchemail varchar(100);

ALTER TABLE eglc_legalcase ADD CONSTRAINT fk_legalcase_concernedbranch FOREIGN KEY (concernedbranch) REFERENCES eglc_concerned_branch_master(id);

alter table eglc_legalcase add column prevpetitiontype bigint;
alter table eglc_legalcase add column prevcourttype bigint;
alter table eglc_legalcase add column prevcaseyear varchar(4);

alter table eglc_legalcase_aud add column prevpetitiontype bigint;
alter table eglc_legalcase_aud add column prevcourttype bigint;
alter table eglc_legalcase_aud add column prevcaseyear varchar(4);

ALTER TABLE eglc_legalcase ADD CONSTRAINT fk_legalcase_prevpetitiontype FOREIGN KEY (prevpetitiontype) REFERENCES eglc_petitiontype_master(id);
ALTER TABLE eglc_legalcase ADD CONSTRAINT fk_legalcase_prevcourttype FOREIGN KEY (prevcourttype) REFERENCES eglc_courttype_master(id);





