DROP TABLE cru_crs_auth_provider_identities CASCADE;

CREATE TABLE cru_crs_auth_provider_identities(
	id uuid NOT NULL PRIMARY KEY,
	crs_app_id uuid references cru_crs_identities(id),
	auth_provider_id text NOT NULL,
	auth_provider_name text NOT NULL
);

INSERT INTO cru_crs_auth_provider_identities(id, crs_app_id, auth_provider_id, auth_provider_name) VALUES('f8f8c217-f977-4503-b3b3-85016f9883c1', 'f8f8c217-f918-4503-b3b3-85016f9883c1','848392a9-3e58-ed5e-0397-7212128cdf16','RELAY');
INSERT INTO cru_crs_auth_provider_identities(id, crs_app_id, auth_provider_id, auth_provider_name) VALUES('f8f8c217-f977-4503-b3b3-85016f988342', 'f8f8c217-f918-4503-b3b3-85016f988343','848392a9-3e58-ed5e-0397-7212128cdf17','EMAIL_ACCOUNT');