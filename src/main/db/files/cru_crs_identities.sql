DROP TABLE cru_crs_identities;

CREATE TABLE cru_crs_identities (
  id uuid NOT NULL PRIMARY KEY,
  system text,
  identifier text,
  user_id uuid
);
