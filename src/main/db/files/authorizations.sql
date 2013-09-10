DROP TABLE authorizations CASCADE;

CREATE TABLE authorizations (
  id uuid NOT NULL PRIMARY KEY,
  conference uuid references conferences(id),
  user_id uuid references users(id),
  can_read_conf_settings boolean,
  can_update_conf_settings boolean,
  can_read_registrations boolean,
  can_update_registrations boolean,
  can_publish_conf boolean
);
