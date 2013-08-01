DROP TABLE cru_crs_external_identities;

CREATE TABLE cru_crs_external_identities(
	id uuid NOT NULL PRIMARY KEY,
	crs_app_id uuid, /*REFERENCES, cru_crs_identities(id),*/
	external_id text NOT NULL,
	external_id_provider_name text NOT NULL
);

INSERT INTO cru_crs_external_identities(id, crs_app_id, external_Id, external_id_provider_name) VALUES('f8f8c217-f977-4503-b3b3-85016f9883c1', 'f8f8c217-f918-4503-b3b3-85016f9883c1','848392a9-3e58-ed5e-0397-7212128cdf16','Relay');