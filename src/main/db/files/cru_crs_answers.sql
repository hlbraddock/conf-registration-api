DROP TABLE cru_crs_answers;

CREATE TABLE cru_crs_answers (
  id uuid NOT NULL PRIMARY KEY,
  block uuid,
  registration uuid,
  value json
);
