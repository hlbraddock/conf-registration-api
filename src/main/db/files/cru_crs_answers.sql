DROP TABLE cru_crs_answers CASCADE;

CREATE TABLE cru_crs_answers (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references cru_crs_registrations(id),
  block_id uuid references cru_crs_blocks(id),
  answer json
);

