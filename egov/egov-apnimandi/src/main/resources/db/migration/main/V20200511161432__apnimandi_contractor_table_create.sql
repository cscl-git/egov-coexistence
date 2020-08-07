CREATE TABLE egam_contractor
(
  id  bigint NOT NULL,
  salutation character varying(10) NOT NULL,
  name character varying(100) NOT NULL,
  address character varying(256) NOT NULL,
  contractsignedon timestamp without time zone NOT NULL, 
  aadhaarno character varying(15) NOT NULL,
  validfromdate timestamp without time zone NOT NULL, 
  validtodate timestamp without time zone NOT NULL,  
  zone bigint NOT NULL,
  maxallowedvendorsno int NOT NULL,
  rentamountperday double precision NOT NULL,
  securityfees double precision NOT NULL,
  duedayofcollectionforeverymonth int NOT NULL,
  penaltyamountperday double precision NOT NULL,
  contractorsharepercentage double precision NOT NULL,
  comment character varying(1024),
  terminateon timestamp without time zone,
  terminateremarks character varying(1024),
  active boolean NOT NULL,
  status bigint NOT NULL,
  state_id bigint,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_contractor PRIMARY KEY (id),
  CONSTRAINT fk_egam_contractor_zone FOREIGN KEY ("zone") REFERENCES egam_zone (id),
  CONSTRAINT fk_egam_contractor_status FOREIGN KEY ("status") REFERENCES egw_status (id),
  CONSTRAINT fk_egam_contractor_State FOREIGN KEY ("state_id")  REFERENCES eg_wf_states (id)
);
CREATE SEQUENCE seq_egam_contractor;

CREATE TABLE egam_contractor_aud
(
  id bigint NOT NULL,
  rev bigint NOT NULL,
  salutation character varying(10),
  name character varying(100),
  address character varying(256),
  contractsignedon timestamp without time zone, 
  aadhaarno character varying(15),
  validfromdate timestamp without time zone, 
  validtodate timestamp without time zone,  
  zone bigint,
  maxallowedvendorsno int,
  rentamountperday double precision,
  securityfees double precision,
  duedayofcollectionforeverymonth int,
  penaltyamountperday double precision,
  contractorsharepercentage double precision,
  comment character varying(1024),
  terminateon timestamp without time zone,
  terminateremarks character varying(1024),
  active boolean,
  createddate timestamp without time zone,
  lastmodifieddate timestamp without time zone,
  createdby bigint,
  lastmodifiedby bigint,
  version numeric DEFAULT 0,
  revtype numeric,
  CONSTRAINT pk_egam_contractor_aud PRIMARY KEY (id, rev)
);

CREATE TABLE egam_contractor_documents
(
  id bigint NOT NULL,
  contractor bigint NOT NULL,
  documenttype bigint NOT NULL,
  filestoreid bigint NOT NULL,
  reffileid character varying(100) NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_contractor_documents PRIMARY KEY (id),
  CONSTRAINT fk_egam_contractor_documents_contractor FOREIGN KEY (contractor) REFERENCES egam_contractor (id),
  CONSTRAINT fk_egam_contractor_documents_documenttype FOREIGN KEY (documenttype) REFERENCES egam_documenttype_master (id),
  CONSTRAINT fk_egam_contractor_documents_filestoreid FOREIGN KEY (filestoreid) REFERENCES eg_filestoremap (id)
);
CREATE SEQUENCE seq_egam_contractor_documents;

CREATE TABLE egam_contractor_documents_aud
(
  id bigint NOT NULL,
  rev bigint NOT NULL,
  contractor bigint,
  documenttype bigint,
  reffileid character varying(100),
  createddate timestamp without time zone,
  lastmodifieddate timestamp without time zone,
  createdby bigint,
  lastmodifiedby bigint,
  version numeric DEFAULT 0,
  revtype numeric,
  CONSTRAINT pk_egam_contractor_documents_aud PRIMARY KEY (id, rev)
);