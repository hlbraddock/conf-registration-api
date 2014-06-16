ALTER TABLE profiles ALTER COLUMN graduation TYPE text;
ALTER TABLE profiles RENAME COLUMN graduation to year_in_school;
ALTER TABLE profiles RENAME COLUMN firstName to first_name;
ALTER TABLE profiles RENAME COLUMN lastName to last_name;
