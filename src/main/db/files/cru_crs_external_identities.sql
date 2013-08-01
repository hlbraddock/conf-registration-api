DROP TABLE cru_crs_external_identities;

CREATE TABLE cru_crs_external_identities (
	id uuid NOT NULL PRIMARY KEY,
	crs_app_id uuid REFERENCES cru_crs_identities(id),
	external_id text NOT NULL,
	external_id_provider_name text NOT NULL
);