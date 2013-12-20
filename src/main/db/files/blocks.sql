DROP TABLE blocks CASCADE;

CREATE TABLE blocks (
  id uuid NOT NULL PRIMARY KEY,
  page_id uuid references pages(id),
  conference_costs_block_id uuid references conference_costs_blocks(id),
  block_type text,
  admin_only boolean,
  required boolean,
  position integer,
  title text,
  content json,
  profile_type text
);

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','paragraphContent',false, false, 0, 'About the conference','"This is a paragraph of text describing this conference."');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('DDA45720-DE87-C419-933A-0187-12B152D2','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','nameQuestion',false, true, 1, 'Your name','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','emailQuestion',false, true, 2, 'Email address','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FD1','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','radioQuestion',false, true, 3, 'Year in school','{"choices" : ["Freshman","Sophomore","Junior","Senior","Super Senior","Grad Student","Do not plan on graduating"]}');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','paragraphContent',false, false, 0, 'We love pets','"Please tell us all about your kitteh."');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('DDA45720-DE87-C419-933A-0187-12B152DC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','nameQuestion',false, true, 1, 'Kittehs name','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','checkboxQuestion',false, false, 2, 'Mah kitteh iz... (check all that apply)','{"choices" : ["Tabby","Tuxedo","Fat","Hunter/huntress","Lethargic","Calico","Aloof","Curious","Swimmer","Eight of nine lives spent"]}');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content, conference_costs_block_id) values('A229C854-6989-F658-7C29-B3DD-034F6FDC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','textQuestion',false, false, 3, 'Preferred cat food brand (or cheezburgerz)','""','c173ed10-198d-11e3-8ffd-0800200c9a66');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','textQuestion',false, false, 0, 'Favorite TV show','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content, conference_costs_block_id) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','checkboxQuestion',false, false, 1, 'Sessions I will attend... (check all that apply)','{"choices" : ["Mens time","Womens time","Wilderness survival","Sword drills 101","What about my cat?"]}','c4b54db0-1993-11e3-8ffd-0800200c9a66');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FDB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','textQuestion',false, false, 2, 'Favorite sport','""');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C444-6989-F658-7C29-B3DD-034F6FDB','707986E8-F4AD-4F6F-E0FD-D28C-2695B163','textQuestion',false, false, 0, 'Favorite poet','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C555-6989-F658-7C29-B3DD-034F6FDB','707986E8-F4AD-4F6F-E0FD-D28C-2695B163','numberQuestion',false, true, 1, 'Poems written in your life','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C666-6989-F658-7C29-B3DD-034F6FDB','707986E8-F4AD-4F6F-E0FD-D28C-2695B163','radioQuestion',false, false, 2, 'Preferred poetry style','{"choices" : ["Haiku","Limerick","Freestyle","Other"] }');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C777-6989-F658-7C29-B3DD-034F6FDB','707986E8-F4AD-4F6F-E0FD-D28C-2695B163','textQuestion',true, false, 3, 'Is capable poet?','""');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A727C854-6989-F658-7C29-B3DD-034F6FDB','ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','textQuestion',false, false, 0, 'Favorite novel','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A728C555-6989-F658-7C29-B3DD-034F6FDB','ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','numberQuestion',false, true, 1, 'Novels written in your life','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A729C666-6989-F658-7C29-B3DD-034F6FDB','ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','radioQuestion',false, false, 2, 'Preferred poetry style','{"choices" : ["Mystery","Romance","Sci-fi","Other"] }');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A739C777-6989-F658-7C29-B3DD-034F6FDB','ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','textQuestion',false, false, 3, 'Is capable author?','""');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content, profile_type) values
('5060D878-4741-4F21-9D25-231D-B86E43EE','506E9FCF-0A49-3F7D-3418-ACBB-F8875BE2','textQuestion', false, true, 0, 'Email address','"Your email address you scoundrel!"', 'email');
