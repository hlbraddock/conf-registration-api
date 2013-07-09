DROP TABLE cru_crs_authorizations;

CREATE TABLE cru_crs_authorizations (
  id uuid NOT NULL PRIMARY KEY,
  conference uuid,
  user_id uuid,
  can_read_conf_settings boolean,
  can_update_conf_settings boolean,
  can_read_registrations boolean,
  can_update_registrations boolean,
  can_publish_conf boolean
);
