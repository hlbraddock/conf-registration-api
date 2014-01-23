DROP TYPE permission_levels CASCADE;

CREATE TYPE permission_levels AS ENUM ('VIEW', 'UPDATE', 'FULL', 'CREATOR');