DROP TABLE permissions CASCADE;

CREATE TABLE permissions (
  id uuid NOT NULL PRIMARY KEY,
  conference_id uuid references conferences(id),
  user_id uuid references users(id),
  email_address text,
  permission_level permission_levels,
  activation_code varchar(41),
  given_by_user_id uuid references users(id),
  last_updated_timestamp timestamp with time zone
);

INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('1f790fa0-770b-11e3-981f-0800200c9a66', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'CREATOR', null, null, 'crs.testuser@crue.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('dcb85040-76e2-11e3-981f-0800200c9a66', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'f8f8c217-f918-4503-b3b3-85016f9883c1', 'UPDATE', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', '14-Aug-2013 15:27:49 UTC', 'ryan.t.carlson@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('2230e3d0-76e3-11e3-981f-0800200c9a66', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'f8f8c217-f918-4503-b3b3-85016f988343', 'VIEW', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', '14-Aug-2013 15:27:50 UTC', 'email.user@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('55dcfe17-4b09-4719-a201-d47b7d3568d4', '1951613E-A253-1AF8-6BC4-C9F1D0B3FA60', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'CREATOR', null, null, 'crs.testuser@crue.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('8c15897c-2c78-41d6-b420-20ae4a3050bb', 'D5878EBA-9B3F-7F33-8355-3193BF4FB698', 'f8f8c217-f918-4503-b3b3-85016f9883c1', 'CREATOR', null, null, 'ryan.t.carlson@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('a078fc14-e0ef-459e-96d1-f904088f136d', '40A342D2-0D99-473A-2C3D-7046BFCDD942', 'f8f8c217-f918-4503-b3b3-85016f9883c1', 'CREATOR', null, null, 'ryan.t.carlson@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('f5e85e25-88e2-402e-b9fb-559332013f44', '50A342D2-0D99-473A-2C3D-7046BFCDD942', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'CREATOR', null, null, 'crs.testuser@crue.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address, activation_code) VALUES('7cc69410-7eeb-11e3-baa7-0800200c9a66', '50A342D2-0D99-473A-2C3D-7046BFCDD942', null, 'VIEW', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', '14-Aug-2014 15:27:50 UTC', 'ryan.t.carlson@cru.org', 'ABC123');