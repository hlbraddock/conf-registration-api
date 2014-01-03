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
  birth_date text,
  gender varchar(1),
  campus text,
  graduation text,
  dormitory text
);

-- INSERT INTO profiles (id, user_id, email, firstName, lastName, phone, street, city, state, zip, birth_date, gender, campus, graduation, dormitory) VALUES ('abcdc217-f918-4503-b3b3-85016f9883c1', 'abcdca08-d7bc-4d92-967c-d82d9d312898', 'c.s.lewis@cru.org', 'Clive', 'Lewis', '407-826-2000', '100 Lake Hart Dr', 'Orlando', 'FL', '32832', '11/29/1898', 'M', 'Oxford', '5/1/1923', 'Hall');
