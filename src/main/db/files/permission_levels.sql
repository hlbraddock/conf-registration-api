DROP TYPE permission_levels CASCADE;

CREATE TYPE permission_levels AS ENUM ( 'NONE', 'VIEW', 'UPDATE', 'FULL', 'CREATOR');