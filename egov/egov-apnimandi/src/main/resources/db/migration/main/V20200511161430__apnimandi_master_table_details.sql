CREATE TABLE egam_documenttype_master
(
  id bigint NOT NULL,
  code character varying(50)  NOT NULL,
  documenttype character varying(100) NOT NULL,
  notes character varying(255),
  ordernumber numeric,
  active boolean NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_documenttype_master PRIMARY KEY (id),
  CONSTRAINT unq_egam_documenttypemaster_document_type UNIQUE (documenttype),
  CONSTRAINT unq_egam_documenttypemaster_code UNIQUE (code)
);
CREATE SEQUENCE seq_egam_documenttype_master;

CREATE TABLE egam_zone
(
  id bigint NOT NULL,
  code character varying(50)  NOT NULL,
  name character varying(100) NOT NULL,
  notes character varying(255),
  ordernumber numeric,
  active boolean NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_zone PRIMARY KEY (id),
  CONSTRAINT unq_egam_zone_code UNIQUE (code),
  CONSTRAINT unq_egam_zone_name UNIQUE (name)
);
CREATE SEQUENCE seq_egam_zone;

CREATE TABLE egam_collection_type
(
  id bigint NOT NULL,
  code character varying(50)  NOT NULL,
  name character varying(100) NOT NULL,
  notes character varying(255),
  ordernumber numeric,
  active boolean NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_collection_type PRIMARY KEY (id),
  CONSTRAINT unq_egam_collection_type_code UNIQUE (code),
  CONSTRAINT unq_egam_collection_type_name UNIQUE (name)
);
CREATE SEQUENCE seq_egam_collection_type;

INSERT INTO egam_documenttype_master(id, code, documenttype, notes, ordernumber, active, createddate, lastmodifieddate, createdby, lastmodifiedby, "version") VALUES
(nextval('seq_egam_documenttype_master'), 'CONTRACT_DOCUMENT', 'Contract Document', 'Contract Document',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_documenttype_master'), 'CONTRACTOR_ADDRESS_PROOF', 'Contractor Address Proof', 'Contractor Address Proof',2,true,now(), now(),1,1,0)
,(nextval('seq_egam_documenttype_master'), 'AADHAAR_CARD', 'Aadhaar Card', 'Aadhaar Card',3,true,now(), now(),1,1,0)
,(nextval('seq_egam_documenttype_master'), 'POSSESSION_LETTER', 'Possession Letter', 'Possession Letterf',4,true,now(), now(),1,1,0)
,(nextval('seq_egam_documenttype_master'), 'SECURITY_DEPOSIT_PROOF', 'Security Deposit Proof', 'Security Deposit Proof',5,true,now(), now(),1,1,0);

INSERT INTO egam_zone(id, code, name, notes, ordernumber, active, createddate, lastmodifieddate, createdby, lastmodifiedby, "version") VALUES
(nextval('seq_egam_zone'), 'ZONE_A', 'Zone-A', 'Zone-A',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_zone'), 'ZONE_B', 'Zone-B', 'Zone-B',2,true,now(), now(),1,1,0)
,(nextval('seq_egam_zone'), 'ZONE_C', 'Zone-C', 'Zone-C',3,true,now(), now(),1,1,0)
,(nextval('seq_egam_zone'), 'ZONE_D', 'Zone-D', 'Zone-D',4,true,now(), now(),1,1,0)
,(nextval('seq_egam_zone'), 'ZONE_E', 'Zone-E', 'Zone-E',5,true,now(), now(),1,1,0);

INSERT INTO egam_collection_type(id, code, name, notes, ordernumber, active, createddate, lastmodifieddate, createdby, lastmodifiedby, "version") VALUES
(nextval('seq_egam_collection_type'), 'DAY_MARKET', 'Day Market', 'Day Market',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_collection_type'), 'APNIMANDI', 'Apnimandi', 'Apnimandi',2,true,now(), now(),1,1,0);