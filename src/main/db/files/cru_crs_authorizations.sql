DROP TABLE cru_crs_authorizations CASCADE;

CREATE TABLE cru_crs_authorizations (
  id uuid NOT NULL PRIMARY KEY,
  conference uuid references cru_crs_conferences(id),
  user_id uuid references cru_crs_users(id),
  can_read_conf_settings boolean,
  can_update_conf_settings boolean,
  can_read_registrations boolean,
  can_update_registrations boolean,
  can_publish_conf boolean
);
