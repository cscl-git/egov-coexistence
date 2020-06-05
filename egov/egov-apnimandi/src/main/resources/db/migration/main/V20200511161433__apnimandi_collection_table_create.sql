CREATE TABLE egam_collection_details
(
	id bigint NOT NULL,
	collectiontype bigint NOT NULL,
	zone bigint NOT NULL,
	collectionformonth int NOT NULL,
	collectionforyear int NOT NULL,
	contractor bigint,
	receiptdate timestamp without time zone NOT NULL,
	servicecategory character varying(50) NOT NULL,
	servicetype character varying(50) NOT NULL,
	payeename character varying(100) NOT NULL,
	paymentmode character varying(50) NOT NULL,
	ddorchequeno character varying(50),
	ddorchequedate timestamp without time zone,
	ifsccode character varying(50),
	bankname character varying(50),
	branchname character varying(50),
	bankcode character varying(50),
	amount double precision NOT NULL,
	comment character varying(1024),
	collectedby bigint NOT NULL,
	collectiondate timestamp without time zone NOT NULL,
	receiptno character varying(100),
	paymentid character varying(100),
	state_id bigint,
	active boolean NOT NULL,
	status bigint NOT NULL,
	createddate timestamp without time zone  NOT NULL,
	lastmodifieddate timestamp without time zone  NOT NULL,
	createdby bigint NOT NULL,
	lastmodifiedby bigint NOT NULL,
	version numeric DEFAULT 0,
	CONSTRAINT pk_egam_collection_details PRIMARY KEY (id),
	CONSTRAINT fk_egam_collection_details_zone FOREIGN KEY ("zone") REFERENCES egam_zone (id),
	CONSTRAINT fk_egam_collection_details_status FOREIGN KEY ("status") REFERENCES egw_status (id),
	CONSTRAINT fk_egam_collection_details_collectiontype FOREIGN KEY ("collectiontype") REFERENCES egam_collection_type (id),
	CONSTRAINT fk_egam_collection_details_contractor FOREIGN KEY ("contractor") REFERENCES egam_contractor (id),
	CONSTRAINT fk_egam_collection_details_State FOREIGN KEY ("state_id")  REFERENCES eg_wf_states (id)
);
CREATE SEQUENCE seq_egam_collection_details;

CREATE TABLE egam_collection_amount_details
(
	id bigint NOT NULL,
	collectiondetails bigint NOT NULL,
	glcodeiddetail character varying(50) NOT NULL,
	accounthead character varying(50) NOT NULL,
	amounttype character varying(20) NOT NULL,
	creditamountdetail double precision NOT NULL,
	createddate timestamp without time zone NOT NULL,
	lastmodifieddate timestamp without time zone NOT NULL,
	createdby bigint NOT NULL,
	lastmodifiedby bigint NOT NULL,
	version numeric DEFAULT 0,
	CONSTRAINT pk_egam_collection_amount_details PRIMARY KEY (id),
	CONSTRAINT fk_egam_collection_amount_details_collectiondetails FOREIGN KEY ("collectiondetails") REFERENCES egam_collection_details (id)
);
CREATE SEQUENCE seq_egam_collection_amount_details;

CREATE TABLE egam_collection_document
(
  id bigint NOT NULL,
  collectiondetails bigint NOT NULL,
  filestoreid bigint NOT NULL,
  reffileid character varying(100) NOT NULL,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_collection_document PRIMARY KEY (id),
  CONSTRAINT fk_egam_collection_document_collectiondetails FOREIGN KEY (collectiondetails) REFERENCES egam_collection_details (id),
  CONSTRAINT fk_egam_contractor_document_filestoreid FOREIGN KEY (filestoreid) REFERENCES eg_filestoremap (id)
);
CREATE SEQUENCE seq_egam_collection_document;

CREATE TABLE egam_collection_details_aud
(
	id bigint NOT NULL,
	rev bigint NOT NULL,
	collectiontype bigint,
	zone bigint,
	collectionformonth int,
	collectionforyear int,
	contractor bigint,
	receiptdate timestamp without time zone,
	servicecategory character varying(50),
	servicetype character varying(50),
	payeename character varying(100),
	paymentmode character varying(50),
	ddorchequeno character varying(50),
	ddorchequedate timestamp without time zone,
	ifsccode character varying(50),
	bankname character varying(50),
	branchname character varying(50),
	bankcode character varying(50),
	amount double precision,
	comment character varying(1024),
	collectedby bigint,
	collectiondate timestamp without time zone,
	receiptno character varying(100),
	paymentid character varying(100),
	active boolean,
	status bigint,
	createddate timestamp without time zone,
	lastmodifieddate timestamp without time zone,
	createdby bigint,
	lastmodifiedby bigint,
	version numeric DEFAULT 0,
	revtype numeric,
	CONSTRAINT pk_egam_collection_details_aud PRIMARY KEY (id, rev)
);

CREATE TABLE egam_collection_amount_details_aud
(
	id bigint NOT NULL,
	rev bigint NOT NULL,
	collectiondetails bigint,
	glcodeiddetail character varying(50),
	accounthead character varying(50),
	amounttype character varying(20),
	creditamountdetail double precision,
	createddate timestamp without time zone,
	lastmodifieddate timestamp without time zone,
	createdby bigint,
	lastmodifiedby bigint,
	version numeric DEFAULT 0,
	revtype numeric,
	CONSTRAINT pk_egam_collection_amount_details_aud PRIMARY KEY (id, rev)
);

CREATE TABLE egam_collection_document_aud
(
  id bigint NOT NULL,
  rev bigint NOT NULL,
  collectiondetails bigint,
  reffileid character varying(100),
  createddate timestamp without time zone,
  lastmodifieddate timestamp without time zone,
  createdby bigint,
  lastmodifiedby bigint,
  version numeric DEFAULT 0,
  revtype numeric,
  CONSTRAINT pk_egam_contractor_document_aud PRIMARY KEY (id, rev)
);