DROP TABLE sessions CASCADE;

CREATE TABLE sessions(
	id uuid NOT NULL PRIMARY KEY,
	auth_code text NOT NULL,
    expiration timestamp with time zone,
	auth_provider_id uuid references auth_provider_identities(id)
);

INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('a8a8c217-f977-4503-b3b3-85016f981234', 'd967b02cc8ec7bba753f88e9264f96ef7944fb49','02-Oct-2012 02:43:14','f8f8c217-f977-4503-b3b3-85016f988342');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('b8a8c217-f977-4503-b3b3-85016f981234', 'fd33c83b97b59dc3884454b7c2b861db03d5399c','02-Oct-2014 02:43:14','f8f8c217-f977-4503-b3b3-85016f9883c1');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('c8a8c217-f977-4503-b3b3-85016f981234', '488aca23cecd6e5b8ac406bf74a46723dd853273','02-Oct-2014 02:43:14','f8f8c217-f977-4503-b3b3-85016f988342');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('d8a8c217-f977-4503-b3b3-85016f981234', '11eac4a91ccb730509cd82d822b5b4dd202de7ff','02-Oct-2014 02:43:14','36f19114-f833-4a26-b7ba-b67052b68cea');
