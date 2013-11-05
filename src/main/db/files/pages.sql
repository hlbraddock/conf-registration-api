DROP TABLE pages CASCADE;

CREATE TABLE pages (
  id uuid NOT NULL PRIMARY KEY,
  title text,
  conference_id uuid NOT NULL references conferences(id),
  position integer
);

INSERT INTO pages (id,conference_id,position,title) VALUES ('7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',0,'About you');
INSERT INTO pages (id,conference_id,position,title) VALUES ('0A00D62C-AF29-3723-F949-95A9-50A0B27C','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',1,'About your cat');
INSERT INTO pages (id,conference_id,position,title) VALUES ('7DAE078F-A131-471E-BB70-5156-B62DDEA5','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',2,'Hobbies and activities');
INSERT INTO pages (id,conference_id,position,title) VALUES ('53CCCC6E-0837-A125-93AA-915F-32D1D2E2','1951613E-A253-1AF8-6BC4-C9F1-D0B3FA60',0,'Personal details');
INSERT INTO pages (id,conference_id,position,title) VALUES ('BF37618E-4F86-2DF5-8AE9-0ED3-BE0ED248','1951613E-A253-1AF8-6BC4-C9F1-D0B3FA60',1,'Cat stories');
INSERT INTO pages (id,conference_id,position,title) VALUES ('707986E8-F4AD-4F6F-E0FD-D28C-2695B163','D5878EBA-9B3F-7F33-8355-3193-BF4FB698',0,'Epic poetry');
INSERT INTO pages (id,conference_id,position,title) VALUES ('ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','D5878EBA-9B3F-7F33-8355-3193-BF4FB698',1,'Short Novels');
INSERT INTO pages (id,conference_id,position,title) VALUES ('5F05F119-D50E-599B-E93D-446A-313CD7C2','40A342D2-0D99-473A-2C3D-7046-BFCDD942',0,'Lorem ipsum dolor sit');
INSERT INTO pages (id,conference_id,position,title) VALUES ('D86E9FCF-0A49-3F7D-3418-ACBB-F8875BE2','40A342D2-0D99-473A-2C3D-7046-BFCDD942',1,'Lorem ipsum dolor');