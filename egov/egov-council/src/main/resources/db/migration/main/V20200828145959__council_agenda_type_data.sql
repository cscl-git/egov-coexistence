INSERT INTO egcncl_agenda_type(id, name, code, isactive, createddate, createdby, lastmodifieddate,lastmodifiedby, version)
    VALUES (nextval('seq_egcncl_agenda_type'), 'Main Agenda', 'Main Agenda', true, now(), 1, now(), 1, 0);
INSERT INTO egcncl_agenda_type(id, name, code, isactive, createddate, createdby, lastmodifieddate,lastmodifiedby, version)
    VALUES (nextval('seq_egcncl_agenda_type'), 'Supplementary Agenda', 'Supplementary Agenda', true, now(), 1, now(), 1, 0);
INSERT INTO egcncl_agenda_type(id, name, code, isactive, createddate, createdby, lastmodifieddate,lastmodifiedby, version)
    VALUES (nextval('seq_egcncl_agenda_type'), 'Table Agenda', 'Table Agenda', true, now(), 1, now(), 1, 0);