

ALTER TYPE profile_type RENAME TO profile_type_x;
CREATE TYPE profile_type AS ENUM ( 'EMAIL', 'NAME', 'PHONE', 'ADDRESS', 'BIRTH_DATE', 'GENDER', 'CAMPUS', 'YEAR_IN_SCHOOL', 'DORMITORY' );

ALTER TABLE blocks RENAME COLUMN profile_type to profile_type_x_col;
ALTER TABLE blocks ADD COLUMN profile_type profile_type;
UPDATE blocks SET profile_type = profile_type_x_col::text::profile_type;

ALTER TABLE blocks DROP COLUMN profile_type_x_col;
DROP TYPE profile_type_x;
