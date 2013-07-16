DROP TABLE cru_crs_blocks;

CREATE TABLE cru_crs_blocks (
  id uuid NOT NULL PRIMARY KEY,
  page_id uuid,
  block_type text,
  admin_only boolean,
  position integer,
  title text,
  content json
);

INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, position, title, content) values('AF60D878-4741-4F21-9D25-231DB86E43EE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','text',false,0, 'Cats name','{}');