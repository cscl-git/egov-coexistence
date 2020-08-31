CREATE TABLE egam_site
(
  id bigint NOT NULL,
  code character varying(50)  NOT NULL,
  name character varying(100) NOT NULL,
  zone bigint NOT NULL,
  notes character varying(255),
  ordernumber numeric,
  active boolean NOT NULL,
  createddate timestamp without time zone  NOT NULL,
  lastmodifieddate timestamp without time zone  NOT NULL,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  version numeric DEFAULT 0,
  CONSTRAINT pk_egam_site PRIMARY KEY (id),
  CONSTRAINT unq_egam_site_code UNIQUE (code),
  CONSTRAINT unq_egam_site_name UNIQUE (name),
  CONSTRAINT fk_egam_site_zone FOREIGN KEY ("zone") REFERENCES egam_zone (id)
);

CREATE SEQUENCE seq_egam_site;

INSERT INTO egam_site(id, code, name, zone, notes, ordernumber, active, createddate, lastmodifieddate, createdby, lastmodifiedby, "version") VALUES
(nextval('seq_egam_site'), 'SECTOR_34', 'Sector 34', (select id from egam_zone where code = 'ZONE_A'), 'Sector 34',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_45C', 'Sector 45C', (select id from egam_zone where code = 'ZONE_A'), 'Sector 45C',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_56', 'Sector 56', (select id from egam_zone where code = 'ZONE_A'), 'Sector 56',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_15', 'Sector 15', (select id from egam_zone where code = 'ZONE_B'), 'Sector 15',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_43A', 'Sector 43A', (select id from egam_zone where code = 'ZONE_B'), 'Sector 43A',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'DHANAS', 'Dhanas', (select id from egam_zone where code = 'ZONE_B'), 'Dhanas',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_46', 'Sector 46', (select id from egam_zone where code = 'ZONE_C'), 'Sector 46',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'RAM_DARBAR', 'Ram Darbar', (select id from egam_zone where code = 'ZONE_C'), 'Ram Darbar',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_40', 'Sector 40', (select id from egam_zone where code = 'ZONE_D'), 'Sector 40',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_52', 'Sector 52', (select id from egam_zone where code = 'ZONE_D'), 'Sector 52',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'DADHUMAJRA', 'Dadhumajra', (select id from egam_zone where code = 'ZONE_D'), 'Dadhumajra',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_29C', 'Sector 29C', (select id from egam_zone where code = 'ZONE_E'), 'Sector 29C',1,true,now(), now(),1,1,0)
,(nextval('seq_egam_site'), 'SECTOR_49', 'Sector 49', (select id from egam_zone where code = 'ZONE_E'), 'Sector 49',1,true,now(), now(),1,1,0)
;

ALTER TABLE egam_collection_details ADD COLUMN site bigint;
ALTER TABLE egam_collection_details_aud ADD COLUMN site bigint;
ALTER TABLE egam_collection_details ADD CONSTRAINT fk_egam_collection_details_site FOREIGN KEY ("site") REFERENCES egam_site (id);