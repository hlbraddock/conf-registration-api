DROP TABLE cru_crs_blocks;

CREATE TABLE cru_crs_blocks (
  id uuid NOT NULL PRIMARY KEY,
  conference_id uuid,
  page_id uuid,
  block_type text,
  admin_only boolean,
  position integer,
  block_description json
);

INSERT INTO cru_crs_blocks(id, conference_id, page_id, block_type, admin_only, position) values('AF60D878-4741-4F21-9D25-231DB86E43EE','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','Foo',false,0);