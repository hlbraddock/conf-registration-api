DROP TABLE cru_crs_blocks CASCADE;

CREATE TABLE cru_crs_blocks (
  id uuid NOT NULL PRIMARY KEY,
  page_id uuid references cru_crs_pages(id),
  block_type text,
  admin_only boolean,
  required boolean,
  position integer,
  title text,
  content json
);

INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','paragraphContent',false, false, 0, 'About the conference','"This is a paragraph of text describing this conference."');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('DDA45720-DE87-C419-933A-0187-12B152D2','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','nameQuestion',false, true, 1, 'Your name','""');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','emailQuestion',false, true, 2, 'Email address','""');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FD1','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','radioQuestion',false, true, 3, 'Year in school','{"choices" : ["Freshman","Sophomore","Junior","Senior","Super Senior","Grad Student","Do not plan on graduating"]}');

INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','paragraphContent',false, false, 0, 'We love pets','"Please tell us all about your kitteh."');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('DDA45720-DE87-C419-933A-0187-12B152DC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','nameQuestion',false, true, 1, 'Kittehs name','""');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','checkboxQuestion',false, false, 2, 'Mah kitteh iz... (check all that apply)','{"choices" : ["Tabby","Tuxedo","Fat","Hunter/huntress","Lethargic","Calico","Aloof","Curious","Swimmer","Eight of nine lives spent"]}');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FDC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','textQuestion',false, false, 3, 'Preferred cat food brand (or cheezburgerz)','""');

INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','textQuestion',false, false, 0, 'Favorite TV show','""');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','checkboxQuestion',false, false, 1, 'Sessions I will attend... (check all that apply)','{"choices" : ["Mens time","Womens time","Wilderness survival","Sword drills 101","What about my cat?"]}');
INSERT INTO cru_crs_blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FDB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','textQuestion',false, false, 2, 'Favorite sport','""');