ALTER TABLE eglc_judgment ADD COLUMN "isdepartmentacted" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_judgment ADD COLUMN "departmentactiondetails" VARCHAR(1024);

ALTER TABLE eglc_judgment_aud ADD COLUMN "isdepartmentacted" BOOLEAN DEFAULT FALSE;
ALTER TABLE eglc_judgment_aud ADD COLUMN "departmentactiondetails" VARCHAR(1024);