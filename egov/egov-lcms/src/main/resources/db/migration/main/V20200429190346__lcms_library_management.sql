-----Inserting Accountdetailtype
INSERT INTO accountdetailtype (id,"name",description,tablename,columnname,attributename,nbroflevels,full_qualified_name,createddate,lastmodifieddate,lastmodifiedby,"version",isactive,createdby) VALUES
(nextval('seq_accountdetailtype'),'lawyer','Standing Counsel','eglc_advocate_master','id','advocate_master_id',1,'org.egov.lcms.masters.entity.AdvocateMaster','2020-04-15 00:00:00.000','2020-04-15 00:00:00.000',NULL,0,true,1);

--EG_ROLE update
update eg_role set internal=true where name in ('Super User');

ALTER TABLE eglc_legalcase ADD COLUMN "isreappealofcase" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_legalcase ADD COLUMN "nodalofficer" VARCHAR(100);
ALTER TABLE eglc_legalcase ADD COLUMN "nodalofficerdepartment" VARCHAR(100);
ALTER TABLE eglc_legalcase ADD COLUMN "estimatepreparedby" VARCHAR(100);
ALTER TABLE eglc_legalcase_aud ADD COLUMN "isreappealofcase" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_legalcase_aud ADD COLUMN "nodalofficer" VARCHAR(100);
ALTER TABLE eglc_legalcase_aud ADD COLUMN "nodalofficerdepartment" VARCHAR(100);
ALTER TABLE eglc_legalcase_aud ADD COLUMN "estimatepreparedby" VARCHAR(100);
alter table eglc_legalcase alter column noticedate drop not null;
alter table eglc_legalcase_aud alter column noticedate drop not null;

CREATE TABLE eglc_documenttype_master
(
  id bigint NOT NULL,
  code character varying(50)  NOT NULL,
  documenttype character varying(100) NOT NULL,
  notes   character varying(255),
  ordernumber numeric,
  active boolean NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_documenttype_master PRIMARY KEY (id),
  CONSTRAINT unq_documenttype_document_type UNIQUE (documenttype),
  CONSTRAINT unq_documenttype_code UNIQUE (code)
);
CREATE SEQUENCE seq_eglc_documenttype_master;

COMMENT ON TABLE eglc_documenttype_master IS 'Document Type Master Table';
COMMENT ON COLUMN eglc_documenttype_master.id IS 'Primary Key';
COMMENT ON COLUMN eglc_documenttype_master.documenttype IS 'Document Type';
COMMENT ON COLUMN eglc_documenttype_master.notes IS 'Notes';
COMMENT ON COLUMN eglc_documenttype_master.ordernumber IS 'Order Number for Sorting';
COMMENT ON COLUMN eglc_documenttype_master.code IS 'Code for Document Type';
COMMENT ON COLUMN eglc_documenttype_master.active IS 'Is Active?';
COMMENT ON COLUMN eglc_documenttype_master.createddate IS 'Created Date';
COMMENT ON COLUMN eglc_documenttype_master.lastmodifieddate IS 'Last Modified Date';
COMMENT ON COLUMN eglc_documenttype_master.createdby IS 'Foreign Key of EG_USER';
COMMENT ON COLUMN eglc_documenttype_master.lastmodifiedby IS 'Foreign Key of EG_USER';
COMMENT ON COLUMN eglc_documenttype_master.version IS 'Version';

CREATE TABLE eglc_library
(
  id bigint NOT NULL,
  title character varying(255)  NOT NULL,
  documenttype bigint NOT NULL,
  filestoreid bigint NOT NULL,
  reffileid character varying(100) NOT NULL,
  active boolean NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_library PRIMARY KEY (id),
  CONSTRAINT fk_library_filestoreid FOREIGN KEY (filestoreid) REFERENCES eg_filestoremap (id)
);
CREATE SEQUENCE seq_eglc_library;

COMMENT ON TABLE  eglc_library IS 'Library Table';
COMMENT ON COLUMN eglc_library.id IS 'Primary Key';
COMMENT ON COLUMN eglc_library.documenttype IS 'Document Type';
COMMENT ON COLUMN eglc_library.title IS 'Title';
COMMENT ON COLUMN eglc_library.filestoreid IS 'Document Id';
COMMENT ON COLUMN eglc_library.active IS 'Is Active?';
COMMENT ON COLUMN eglc_library.createddate IS 'Created Date';
COMMENT ON COLUMN eglc_library.lastmodifieddate IS 'Last Modified Date';
COMMENT ON COLUMN eglc_library.createdby IS 'Foreign Key of EG_USER';
COMMENT ON COLUMN eglc_library.lastmodifiedby IS 'Foreign Key of EG_USER';
COMMENT ON COLUMN eglc_library.version IS 'Version';

CREATE TABLE eglc_library_aud
(
  id bigint NOT NULL,
  rev bigint NOT NULL,
  title character varying(255),
  documenttype bigint,
  reffileid character varying(100),
  active boolean,
  lastmodifieddate timestamp without time zone,
  lastmodifiedby bigint,
  revtype numeric
);
ALTER TABLE ONLY eglc_library_aud ADD CONSTRAINT pk_eglc_library_aud PRIMARY KEY (id, rev);

INSERT INTO eglc_documenttype_master(id, code, documenttype, notes, ordernumber, active, createddate, lastmodifieddate, createdby, lastmodifiedby, "version") VALUES
(nextval('seq_eglc_documenttype_master'), 'ACT', 'ACT', 'ACT',1,true,now(), now(),1,1,0)
,(nextval('seq_eglc_documenttype_master'), 'Legal Information', 'Legal Information', 'Legal Information',2,true,now(), now(),1,1,0);