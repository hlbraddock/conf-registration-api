DROP TABLE cru_crs_answers CASCADE;

CREATE TABLE cru_crs_answers (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references cru_crs_registrations(id),
  block_id uuid references blocks(id),
  answer json
);

INSERT INTO cru_crs_answers (id,registration_id,block_id,answer) VALUES ('441AD805-7AA6-4B20-8315-8F1390DC4A9E','A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','AF60D878-4741-4F21-9D25-231DB86E43EE','{ "Name": "Alexander Solzhenitsyn"}');