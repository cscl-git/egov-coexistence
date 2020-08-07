DROP TABLE IF EXISTS eg_wf_dept_desg_map;

CREATE TABLE eg_wf_dept_desg_map (
	id int8 NOT NULL,
	objecttype varchar(30) NOT NULL,
	currentstate varchar(100) NULL,
	nextdepartment varchar(50) NULL,
	nextdesignation varchar(2048) NULL,
	additionalrule varchar(50) NULL,
	"version" int4 NULL,
	CONSTRAINT eg_wf_dept_desg_map_pkey PRIMARY KEY (id)
);

--CREATE SEQUENCE seq_eg_wf_dept_desg_map INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1;