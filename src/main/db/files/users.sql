DROP TABLE users CASCADE ;

CREATE TABLE users (
  id uuid NOT NULL PRIMARY KEY,
  first_name text,
  last_name text,
  phone_number text,
  email_address text
);

INSERT INTO users(id) VALUES('f8f8c217-f918-4503-b3b3-85016f9883c1');
INSERT INTO users(id) VALUES('f8f8c217-f918-4503-b3b3-85016f988343');
INSERT INTO users(id) VALUES('dbc6a808-d7bc-4d92-967c-d82d9d312898');