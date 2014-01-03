DROP TABLE registrations CASCADE;

CREATE TABLE registrations (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid references users(id),
  conference_id uuid references conferences(id),
  total_due decimal default 0,
  completed boolean default false,
  completed_timestamp timestamp with time zone
);

INSERT INTO registrations (id,user_id,conference_id) VALUES ('A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','dbc6a808-d7bc-4d92-967c-d82d9d312898','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309');
INSERT INTO registrations (id,user_id,conference_id) VALUES ('B2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','f8f8c217-f918-4503-b3b3-85016f988343','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309');
INSERT INTO registrations (id,user_id,conference_id) VALUES ('AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111','f8f8c217-f918-4503-b3b3-85016f9883c1','1951613E-A253-1AF8-6BC4-C9F1D0B3FA60');
