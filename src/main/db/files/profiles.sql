DROP TABLE profiles CASCADE ;

CREATE TABLE profiles (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid references users(id),
  email text,
  firstName text,
  lastName text,
  phone text,
  street text,
  city text,
  state text,
  zip varchar(5),
  birth_date date,
  gender varchar(1),
  campus text,
  graduation date,
  dormitory text
);

INSERT INTO profiles
(id, user_id, email, firstName, lastName, phone, street, city, state, zip, birth_date, gender, campus, graduation, dormitory)
VALUES
('dbc7a808-d7bc-4d92-967c-d82d9d312898', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'test.user@cru.org', 'Test', 'User', '407-826-2000', '100 Lake Hart Dr', 'Orlando', 'FL', '32832', '12/29/2001', 'M', 'UCF', '5/1/1993', 'Dorm1');
