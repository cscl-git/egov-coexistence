ALTER TABLE eglc_judgment ADD COLUMN "isdepartmentacted" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_judgment ADD COLUMN "departmentactiondetails" VARCHAR(1024);

ALTER TABLE eglc_judgment_aud ADD COLUMN "isdepartmentacted" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_judgment_aud ADD COLUMN "departmentactiondetails" VARCHAR(1024);

ALTER TABLE eglc_employeehearing ADD COLUMN "employeename" VARCHAR(200);
ALTER TABLE eglc_employeehearing_aud ADD COLUMN "employeename" VARCHAR(200);

ALTER TABLE eglc_employeehearing RENAME COLUMN "employee" TO "employeeid";
ALTER TABLE eglc_employeehearing_aud RENAME COLUMN "employee" TO "employeeid";

ALTER TABLE eglc_employeehearing DROP CONSTRAINT fk_employeehearing_employee;