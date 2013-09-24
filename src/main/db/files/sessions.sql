DROP TABLE sessions CASCADE;

CREATE TABLE sessions(
	id uuid NOT NULL PRIMARY KEY,
	auth_code text NOT NULL,
    expiration timestamp with time zone,
	auth_provider_id uuid references auth_provider_identities(id)
);

IDENTITY, auth code, timestamp, auth provider id (fk)

INSERT INTO sessions(id, auth_code, expiration, user_auth_provider_id) VALUES ('b8a8c217-f977-4503-b3b3-85016f981234', 'c4d5c217-f918-4503-b3b3-85016f984567',''02-Oct-2014 02:43:14'','f8f8c217-f977-4503-b3b3-85016f9883c1');
