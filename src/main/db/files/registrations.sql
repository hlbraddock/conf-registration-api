DROP TABLE cru_crs_registrations CASCADE;
DROP TABLE registrations CASCADE;

CREATE TABLE registrations (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid references users(id),
  conference_id uuid references conferences(id)
);

INSERT INTO registrations (id,user_id,conference_id) VALUES ('A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','dbc6a808-d7bc-4d92-967c-d82d9d312898','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309');
