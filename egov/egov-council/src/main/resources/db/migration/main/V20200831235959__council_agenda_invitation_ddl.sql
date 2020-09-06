create table egcncl_agenda_invitation (
	id bigint,	
	meetingNumber varchar(25) not null,
	meetingDate timestamp without time zone,
	meetingTime varchar(50),
	meetingLocation varchar(100),
	message varchar(500),
	filestoreid bigint,
	createddate timestamp without time zone DEFAULT ('now'::text)::date NOT NULL,
	lastmodifieddate timestamp without time zone,
	createdby bigint NOT NULL,
	lastmodifiedby bigint,
	version bigint DEFAULT 0
);

alter table egcncl_agenda_invitation add constraint pk_egcncl_agenda_invitation primary key (id);
create sequence seq_egcncl_agenda_invitation;