INSERT INTO users(id, first_name, last_name, email_address) VALUES('f8f8c217-f918-4503-b3b3-85016f9883c1', 'Ryan', 'Carlson', 'ryan.t.carlson@cru.org');
INSERT INTO users(id) VALUES('f8f8c217-f918-4503-b3b3-85016f988343');
INSERT INTO users(id, first_name, last_name, email_address) VALUES('dbc6a808-d7bc-4d92-967c-d82d9d312898', 'Test', 'User', 'test.user@cru.org');
INSERT INTO users(id) VALUES('abcdca08-d7bc-4d92-967c-d82d9d312898');
INSERT INTO users(id) VALUES('1234c217-f918-4503-b3b3-85016f988343');

INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name, username, email) VALUES('f8f8c217-f977-4503-b3b3-85016f9883c1', 'f8f8c217-f918-4503-b3b3-85016f9883c1','848392a9-3e58-ed5e-0397-7212128cdf16','RELAY','ryan.t.carlson@cru.org','ryan.t.carlson@cru.org');
INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name, username, email) VALUES('f8f8c217-f977-4503-b3b3-85016f988342', 'f8f8c217-f918-4503-b3b3-85016f988343','848392a9-3e58-ed5e-0397-7212128cdf17','FACEBOOK', 'test.user@cru.org', 'test.user@cru.org');
INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name, username, email) VALUES('36f19114-f833-4a26-b7ba-b67052b68cea', 'dbc6a808-d7bc-4d92-967c-d82d9d312898','05218422-6bbf-47fb-897c-371c91f87076','RELAY','crs.testuser@crue.org','crs.testuser@crue.org');
INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_name) VALUES ('1234c217-f918-4503-b3b3-85016f988342', '1234c217-f918-4503-b3b3-85016f988343','1234c217-f918-4503-b3b3-85016f988343','NONE');

INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('a8a8c217-f977-4503-b3b3-85016f981234', 'd967b02cc8ec7bba753f88e9264f96ef7944fb49','02-Oct-2012 02:43:14 UTC','f8f8c217-f977-4503-b3b3-85016f988342');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('b8a8c217-f977-4503-b3b3-85016f981234', 'fd33c83b97b59dc3884454b7c2b861db03d5399c','02-Oct-2014 02:43:14 UTC','f8f8c217-f977-4503-b3b3-85016f9883c1');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('c8a8c217-f977-4503-b3b3-85016f981234', '488aca23cecd6e5b8ac406bf74a46723dd853273','02-Oct-2014 02:43:14 UTC','f8f8c217-f977-4503-b3b3-85016f988342');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('d8a8c217-f977-4503-b3b3-85016f981234', '11eac4a91ccb730509cd82d822b5b4dd202de7ff','02-Oct-2014 02:43:14 UTC','36f19114-f833-4a26-b7ba-b67052b68cea');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES ('e8a8c217-f977-4503-b3b3-85016f981234', '123aca23cecd6e5b8ac406bf74a46723dd853273','02-Oct-2013 02:43:14 UTC','f8f8c217-f977-4503-b3b3-85016f988342');
INSERT INTO sessions(id, auth_code, expiration, auth_provider_id) VALUES
('1234c217-f977-4503-b3b3-85016f981234', '1234c4a91ccb730509cd82d822b5b4dd202de7ff','02-Oct-2016 02:43:14 UTC','1234c217-f918-4503-b3b3-85016f988342');

INSERT INTO conference_costs (id,base_cost,minimum_deposit) VALUES ('42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', '50.00', '10.00');
INSERT INTO conference_costs (id,base_cost,minimum_deposit,early_registration_discount,early_registration_cutoff,early_registration_amount) VALUES ('D5878EBA-9B3F-7F33-8355-3193BF4FB698', '75.00', '0.00', true, '31-Mar-2014 23:59:59 UTC', '15.00');
INSERT INTO conference_costs (id,base_cost,minimum_deposit) VALUES ('1951613E-A253-1AF8-6BC4-C9F1D0B3FA60', '75.00', '20.00');
INSERT INTO conference_costs (id,base_cost,minimum_deposit) VALUES ('40A342D2-0D99-473A-2C3D-7046BFCDD942', '125.00', '20.00');
INSERT INTO conference_costs (id,base_cost,minimum_deposit) VALUES ('50A342D2-0D99-473A-2C3D-7046BFCDD942', '125.00', '20.00');

INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, location_name, location_address, location_city, location_state, location_zip_code) VALUES ('42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309','Northern Michigan Fall Extravaganza','29-Aug-2014 22:30:00 UTC','31-Aug-2014 16:00:00 UTC','11-Apr-2013 01:58:35 UTC','29-Aug-2014 21:00:00 UTC','80', 'Black Bear Camp', '5287 St Rt 17', 'Marquette', 'MI', '42302');
INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('1951613E-A253-1AF8-6BC4-C9F1D0B3FA60','1951613E-A253-1AF8-6BC4-C9F1D0B3FA60','Miami University Fall Retreat','12-Sep-2014 22:45:00 UTC','14-Sep-2014 17:30:00 UTC','08-Mar-2013 00:51:50 UTC','12-Sep-2014 03:59:59 UTC','197');
INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('D5878EBA-9B3F-7F33-8355-3193BF4FB698','D5878EBA-9B3F-7F33-8355-3193BF4FB698','New York U. Retreat Weekend','31-Oct-2014 23:00:00','02-Sept-2014 16:00:00 UTC','17-Mar-2013 12:29:14','31-Oct-2014 23:00:00 UTC','116');
INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('40A342D2-0D99-473A-2C3D-7046BFCDD942','40A342D2-0D99-473A-2C3D-7046BFCDD942','Fall Beach Weekend','31-Oct-2014 16:00:00 UTC','2-Nov-2014 09:00:00 UTC','1-Jun-2014 04:00:00 UTC','31-Oct-2014 15:50:00 UTC','289');
INSERT INTO conferences (id,conference_costs_id,name,description,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, location_name, location_address, location_city, location_state, location_zip_code) VALUES
('50A342D2-0D99-473A-2C3D-7046BFCDD942','50A342D2-0D99-473A-2C3D-7046BFCDD942','Winter Beach Weekend!','Training for Missionaries','06-Aug-2013 18:44:48','12-Oct-2013 14:37:10','10-Apr-2013 22:01:21','04-May-2016 19:22:37', '200', 'LifeWay Ridgecrest Conference Center', '1  Ridgecrest Drive', ' Ridgecrest', 'NC', '28770');

INSERT INTO pages (id,conference_id,position,title) VALUES ('7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',0,'About you');
INSERT INTO pages (id,conference_id,position,title) VALUES ('0A00D62C-AF29-3723-F949-95A9-50A0B27C','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',1,'About your cat');
INSERT INTO pages (id,conference_id,position,title) VALUES ('7DAE078F-A131-471E-BB70-5156-B62DDEA5','42E4C1B2-0CC1-89F7-9F4B-6BC3-E0DB5309',2,'Hobbies and activities');
INSERT INTO pages (id,conference_id,position,title) VALUES ('53CCCC6E-0837-A125-93AA-915F-32D1D2E2','1951613E-A253-1AF8-6BC4-C9F1-D0B3FA60',0,'Personal details');
INSERT INTO pages (id,conference_id,position,title) VALUES ('BF37618E-4F86-2DF5-8AE9-0ED3-BE0ED248','1951613E-A253-1AF8-6BC4-C9F1-D0B3FA60',1,'Cat stories');
INSERT INTO pages (id,conference_id,position,title) VALUES ('707986E8-F4AD-4F6F-E0FD-D28C-2695B163','D5878EBA-9B3F-7F33-8355-3193-BF4FB698',0,'Epic poetry');
INSERT INTO pages (id,conference_id,position,title) VALUES ('ECB5B34F-D24D-83C8-2954-2179-AC4C38F2','D5878EBA-9B3F-7F33-8355-3193-BF4FB698',1,'Short Novels');
INSERT INTO pages (id,conference_id,position,title) VALUES ('5F05F119-D50E-599B-E93D-446A-313CD7C2','40A342D2-0D99-473A-2C3D-7046-BFCDD942',0,'Lorem ipsum dolor sit');
INSERT INTO pages (id,conference_id,position,title) VALUES ('D86E9FCF-0A49-3F7D-3418-ACBB-F8875BE2','40A342D2-0D99-473A-2C3D-7046-BFCDD942',1,'Lorem ipsum dolor');
INSERT INTO pages (id,conference_id,position,title) VALUES ('506E9FCF-0A49-3F7D-3418-ACBB-F8875BE2','50A342D2-0D99-473A-2C3D-7046BFCDD942',1,'Lorem ipsum dolor sit amet');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','paragraphContent',false, false, 0, 'About the conference','"This is a paragraph of text describing this conference."');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('DDA45720-DE87-C419-933A-0187-12B152D2','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','nameQuestion',false, true, 1, 'Your name','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AE','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','emailQuestion',false, true, 2, 'Email address','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FD1','7A52AF36-2F3C-5E45-9F76-0AF1-0FF50BB8','radioQuestion',false, true, 3, 'Year in school','{"choices" : ["Freshman","Sophomore","Junior","Senior","Super Senior","Grad Student","Do not plan on graduating"]}');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','paragraphContent',false, false, 0, 'We love pets','"Please tell us all about your kitteh."');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('DDA45720-DE87-C419-933A-0187-12B152DC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','nameQuestion',false, true, 1, 'Kittehs name','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','checkboxQuestion',false, false, 2, 'Mah kitteh iz... (check all that apply)','{"choices" : ["Tabby","Tuxedo","Fat","Hunter/huntress","Lethargic","Calico","Aloof","Curious","Swimmer","Eight of nine lives spent"]}');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('A229C854-6989-F658-7C29-B3DD-034F6FDC','0A00D62C-AF29-3723-F949-95A9-50A0B27C','textQuestion',false, false, 3, 'Preferred cat food brand (or cheezburgerz)','""');

INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('AF60D878-4741-4F21-9D25-231D-B86E43EB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','textQuestion',false, false, 0, 'Favorite TV show','""');
INSERT INTO blocks(id, page_id, block_type, admin_only, required, position, title, content) values('F774EA5C-8E44-25DC-9169-2F14-1C57E3AB','7DAE078F-A131-471E-BB70-5156-B62DDEA5','checkboxQuestion',false, false, 1, 'Sessions I will attend... (check all that apply)','{"choices" : ["Mens time","Womens time","Wilderness survival","Sword drills 101","What about my cat?"]}');
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
('5060D878-4741-4F21-9D25-231D-B86E43EE','506E9FCF-0A49-3F7D-3418-ACBB-F8875BE2','emailQuestion', false, true, 0, 'Email address','"Your email address you scoundrel!"', 'EMAIL');

INSERT INTO registrations (id,user_id,conference_id, total_due) VALUES ('A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','dbc6a808-d7bc-4d92-967c-d82d9d312898','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 120.00);
INSERT INTO registrations (id,user_id,conference_id) VALUES ('B2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','f8f8c217-f918-4503-b3b3-85016f988343','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309');
INSERT INTO registrations (id,user_id,conference_id) VALUES ('AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111','f8f8c217-f918-4503-b3b3-85016f9883c1','1951613E-A253-1AF8-6BC4-C9F1D0B3FA60');

INSERT INTO answers (id,registration_id,block_id,answer) VALUES ('441AD805-7AA6-4B20-8315-8F1390DC4A9E','A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7','AF60D878-4741-4F21-9D25-231DB86E43EE','{ "Name": "Alexander Solzhenitsyn"}');

INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp, payment_type) VALUES ('8492F4A8-C7DC-4C0A-BB9E-67E6DCB22222', 'AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111', '2205342461', 'Billy User', '04', '2014', '1111', 20.00, '21-Aug-2013 19:22:07 UTC', 'CREDIT_CARD');
INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp, payment_type) VALUES ('8492F4A8-C7DC-4C0A-BB9E-67E6DCB33333', 'AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111', '1', 'Billy User', '04', '2014', '1111', 55.00, '1-Oct-2013 11:02:11 UTC', 'CREDIT_CARD');

INSERT INTO registration_views(id, conference_id, created_by_user_id, name, visible_block_ids) VALUES('11cfdedf-febc-4011-9b48-44d36bf94997', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'No cats', '["AF60D878-4741-4F21-9D25-231DB86E43EE","DDA45720-DE87-C419-933A-018712B152D2"]');
INSERT INTO registration_views(id, conference_id, created_by_user_id, name, visible_block_ids) VALUES('70daba1c-c252-4c3f-a76c-f7c3d97941ab', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'Name and email', '["AF60D878-4741-4F21-9D25-231DB86E43EB","A229C854-6989-F658-7C29-B3DD034F6FDB"]');

INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('1f790fa0-770b-11e3-981f-0800200c9a66', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'CREATOR', null, null, 'crs.testuser@crue.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('dcb85040-76e2-11e3-981f-0800200c9a66', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'f8f8c217-f918-4503-b3b3-85016f9883c1', 'UPDATE', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', '14-Aug-2013 15:27:49 UTC', 'ryan.t.carlson@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('2230e3d0-76e3-11e3-981f-0800200c9a66', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'f8f8c217-f918-4503-b3b3-85016f988343', 'VIEW', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', '14-Aug-2013 15:27:50 UTC', 'email.user@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('55dcfe17-4b09-4719-a201-d47b7d3568d4', '1951613E-A253-1AF8-6BC4-C9F1D0B3FA60', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'CREATOR', null, null, 'crs.testuser@crue.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('8c15897c-2c78-41d6-b420-20ae4a3050bb', 'D5878EBA-9B3F-7F33-8355-3193BF4FB698', 'f8f8c217-f918-4503-b3b3-85016f9883c1', 'CREATOR', null, null, 'ryan.t.carlson@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('a078fc14-e0ef-459e-96d1-f904088f136d', '40A342D2-0D99-473A-2C3D-7046BFCDD942', 'f8f8c217-f918-4503-b3b3-85016f9883c1', 'CREATOR', null, null, 'ryan.t.carlson@cru.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address) VALUES('f5e85e25-88e2-402e-b9fb-559332013f44', '50A342D2-0D99-473A-2C3D-7046BFCDD942', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'CREATOR', null, null, 'crs.testuser@crue.org');
INSERT INTO permissions(id, conference_Id, user_id, permission_level, given_by_user_id, last_updated_timestamp, email_address, activation_code) VALUES('7cc69410-7eeb-11e3-baa7-0800200c9a66', '50A342D2-0D99-473A-2C3D-7046BFCDD942', null, 'VIEW', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', '14-Aug-2014 15:27:50 UTC', 'ryan.t.carlson@cru.org', 'ABC123');

INSERT INTO profiles
(id, user_id, email, firstName, lastName, phone, address1, address2, city, state, zip, birth_date, gender, campus, graduation, dormitory)
VALUES
('dbc7a808-d7bc-4d92-967c-d82d9d312898', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'test.user@cru.org', 'Test', 'User', '407-826-2000', '100 Lake Hart Drive', 'MailStop 2400', 'Orlando', 'FL', '32832', '12/29/2001', 'M', 'UCF', '5/1/1993', 'Dorm1');
