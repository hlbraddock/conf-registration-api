DROP TABLE cru_crs_pages;

CREATE TABLE cru_crs_pages (
  id uuid NOT NULL PRIMARY KEY,
  name text,
  conference_id uuid NOT NULL /*references cru_crs_conferences(id)*/,
  position integer
);

INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',0,'About you');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('0A00D62C-AF29-3723-F949-95A9-50A0B27C','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',1,'About your cat');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('7DAE078F-A131-471E-BB70-5156-B62DDEA5','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',2,'Hobbies and activities');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('53CCCC6E-0837-A125-93AA-915F-32D1D2E2','1951613E-A253-1AF8-6BC4-C9F1-D0B3FA60',0,'Personal details');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('BF37618E-4F86-2DF5-8AE9-0ED3-BE0ED248','1951613E-A253-1AF8-6BC4-C9F1-D0B3FA60',1,'Cat stories');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('707986E8-F4AD-4F6F-E0FD-D28C-2695B163','D5878EBA-9B3F-7F33-8355-3193-BF4FB698',0,'Epic poetry');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','1CFA829F-2C3A-F803-A966-9A65-10EE2F33',0,'Lorem ipsum dolor');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('A70ECFCE-4967-6EAF-4E15-5A02-FD57D7C7','1CFA829F-2C3A-F803-A966-9A65-10EE2F33',1,'Lorem ipsum dolor sit amet, consectetuer');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('D6BD291C-F82E-3221-387E-89E6-F5372B60','1CFA829F-2C3A-F803-A966-9A65-10EE2F33',2,'Lorem ipsum');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('1D3AEFA7-28A4-AC82-36BD-42DA-64D1AD3B','67384634-F379-DE07-86DA-6B89-ABC6FAB5',0,'Lorem ipsum dolor sit amet,');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('76970098-BD37-56BC-2814-6CBA-AD580137','67384634-F379-DE07-86DA-6B89-ABC6FAB5',1,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('CF7D8086-40CA-B8C4-A1A7-C519-A9ABD229','67384634-F379-DE07-86DA-6B89-ABC6FAB5',2,'Lorem ipsum');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('A79B0FF0-BAC4-70F8-B650-7EA1-96EB98C3','67384634-F379-DE07-86DA-6B89-ABC6FAB5',3,'Lorem ipsum');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('5F05F119-D50E-599B-E93D-446A-313CD7C2','40A342D2-0D99-473A-2C3D-7046-BFCDD942',0,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('D86E9FCF-0A49-3F7D-3418-ACBB-F8875BE2','40A342D2-0D99-473A-2C3D-7046-BFCDD942',1,'Lorem ipsum dolor');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('41D4DC47-0DB0-4839-9B36-7F5F-8C43B77F','23120BC5-2005-C434-47B0-77E5-137C2DB5',0,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('43607E15-92A2-53C1-7296-78E4-D2F5AFEF','23120BC5-2005-C434-47B0-77E5-137C2DB5',1,'Lorem ipsum');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('40A210C4-AE9D-AB4C-4B4D-A9BD-9078E7B3','23120BC5-2005-C434-47B0-77E5-137C2DB5',2,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('65961D00-7EA2-377E-B494-06D0-E6F522C8','847B4710-B450-C003-7F99-2055-E0AFD206',0,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('6EA3FAAD-5D0C-4F89-8C9E-AAD1-A23CFB46','847B4710-B450-C003-7F99-2055-E0AFD206',1,'Lorem ipsum dolor sit amet,');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('F99946F1-7B66-30E0-ADB4-BCED-0BDB046F','847B4710-B450-C003-7F99-2055-E0AFD206',2,'Lorem ipsum dolor sit amet, consectetuer');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('F92683D3-A47A-3840-39B0-8904-7C4B2492','F3CA81A4-1B89-A12C-2710-8B06-E1C49FF8',0,'Lorem ipsum dolor sit amet, consectetuer');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('B86F6A9A-14D9-9ED2-ADB3-EFBB-47E8006B','F3CA81A4-1B89-A12C-2710-8B06-E1C49FF8',1,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('CA064B78-8117-F41D-F5BF-1738-5C8523FE','F3CA81A4-1B89-A12C-2710-8B06-E1C49FF8',2,'Lorem ipsum dolor sit amet, consectetuer');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('F52ADB3E-CAE1-CB12-703E-4C40-49389278','DDFE8883-9AFC-21DB-DD1B-EE08-D000C3B9',0,'Lorem ipsum dolor sit');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('92446311-12EC-4044-4290-6950-DD9F526E','DDFE8883-9AFC-21DB-DD1B-EE08-D000C3B9',1,'Lorem ipsum dolor');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('A3BD5CE8-ABCB-F102-A094-6A14-409668B0','DDFE8883-9AFC-21DB-DD1B-EE08-D000C3B9',2,'Lorem ipsum dolor sit amet,');
INSERT INTO cru_crs_pages (id,conference_id,position,name) VALUES ('6E13DAD8-6988-9B2A-460E-7427-E9D64E2B','DDFE8883-9AFC-21DB-DD1B-EE08-D000C3B9',3,'Lorem ipsum dolor sit amet, consectetuer');