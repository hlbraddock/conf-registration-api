DROP TABLE cru_crs_auth_provider_identities CASCADE;
DROP TABLE auth_provider_identities CASCADE;

CREATE TABLE auth_provider_identities(
	id uuid NOT NULL PRIMARY KEY,
	crs_id uuid references users(id),
	auth_provider_name text NOT NULL,
	user_auth_provider_id text NOT NULL,
	auth_provider_user_access_token text,
	username text,
	first_name text,
	last_name text
);

INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name, username) VALUES('f8f8c217-f977-4503-b3b3-85016f9883c1', 'f8f8c217-f918-4503-b3b3-85016f9883c1','848392a9-3e58-ed5e-0397-7212128cdf16','RELAY','ryan.t.carlson@cru.org');
INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name) VALUES('f8f8c217-f977-4503-b3b3-85016f988342', 'f8f8c217-f918-4503-b3b3-85016f988343','848392a9-3e58-ed5e-0397-7212128cdf17','EMAIL_ACCOUNT');
INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name, username) VALUES('36f19114-f833-4a26-b7ba-b67052b68cea', 'dbc6a808-d7bc-4d92-967c-d82d9d312898','05218422-6bbf-47fb-897c-371c91f87076','RELAY','crs.testuser@crue.org');