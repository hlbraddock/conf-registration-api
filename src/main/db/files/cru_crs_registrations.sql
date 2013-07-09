DROP TABLE cru_crs_registrations;

CREATE TABLE cru_crs_registrations (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid,
  conference uuid
);
