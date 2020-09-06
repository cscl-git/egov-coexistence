Create table egcncl_agenda_type( 
	id bigint,
	name varchar(100)NOT NULL,
	code varchar(20),
	isActive boolean NOT NULL default true,
	createddate timestamp without time zone,
	createdby bigint,
	lastmodifieddate timestamp without time zone,
	lastmodifiedby bigint,
	version bigint
);
alter table egcncl_agenda_type add constraint pk_egcncl_agenda_type primary key (id);
create sequence seq_egcncl_agenda_type;


