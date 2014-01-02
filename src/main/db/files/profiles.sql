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
  zip text,
  birth_date text,
  gender text,
  campus text,
  graduation text,
  dormitory text
);

-- INSERT INTO profiles(id) VALUES('ABf8c217-f918-4503-b3b3-85016f9883c1', 'dbc6a808-d7bc-4d92-967c-d82d9d312898');
