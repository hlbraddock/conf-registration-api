DROP TABLE cru_crs_registrations CASCADE;

CREATE TABLE cru_crs_registrations (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid references cru_crs_users(id),
  conference_id uuid references cru_crs_conferences(id)
);

