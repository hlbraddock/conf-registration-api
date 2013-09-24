DROP TABLE answers CASCADE;

CREATE TABLE answers (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references registrations(id),
  block_id uuid references blocks(id),
  answer json
);

INSERT INTO answers (id,registration_id,block_id,answer) VALUES ('441AD805-7AA6-4B20-8315-8F1390DC4A9E','A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','AF60D878-4741-4F21-9D25-231DB86E43EE','{ "Name": "Alexander Solzhenitsyn"}');