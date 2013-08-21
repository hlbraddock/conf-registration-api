DROP TABLE cru_crs_users CASCADE ;

CREATE TABLE cru_crs_users (
  id uuid NOT NULL PRIMARY KEY,
  first_name text,
  last_name text,
  spouse_user_id uuid,
  birth_date date,
  contact_email text,
  home_address_line_1 text,
  home_address_line_2 text,
  home_address_city text,
  home_address_state text,
  home_address_postal text,
  home_address_country text
);

INSERT INTO cru_crs_users(id) VALUES('f8f8c217-f918-4503-b3b3-85016f9883c1');
INSERT INTO cru_crs_users(id) VALUES('f8f8c217-f918-4503-b3b3-85016f988343');
INSERT INTO cru_crs_users(id) VALUES('dbc6a808-d7bc-4d92-967c-d82d9d312898');