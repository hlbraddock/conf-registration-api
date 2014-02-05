DROP TABLE conferences CASCADE;

CREATE TABLE conferences (
  id uuid NOT NULL PRIMARY KEY,
  conference_costs_id uuid references conference_costs(id),
  name text,
  description text,
  event_start_time timestamp with time zone,
  event_end_time timestamp with time zone,
  registration_start_time timestamp with time zone,
  registration_end_time timestamp with time zone,
  total_slots integer default NULL,
  contact_person_name text,
  contact_person_email text,
  contact_person_phone text,
  location_name text,
  location_address text,
  location_city text,
  location_state text,
  location_zip_code varchar(5),
  require_login boolean
);

INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, location_name, location_address, location_city, location_state, location_zip_code) VALUES ('42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309','42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309','Northern Michigan Fall Extravaganza','29-Aug-2014 22:30:00 UTC','31-Aug-2014 16:00:00 UTC','11-Apr-2013 01:58:35 UTC','29-Aug-2014 21:00:00 UTC','80', 'Black Bear Camp', '5287 St Rt 17', 'Marquette', 'MI', '42302');
INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('1951613E-A253-1AF8-6BC4-C9F1D0B3FA60','1951613E-A253-1AF8-6BC4-C9F1D0B3FA60','Miami University Fall Retreat','12-Sep-2014 22:45:00 UTC','14-Sep-2014 17:30:00 UTC','08-Mar-2013 00:51:50 UTC','12-Sep-2014 03:59:59 UTC','197');
INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('D5878EBA-9B3F-7F33-8355-3193BF4FB698','D5878EBA-9B3F-7F33-8355-3193BF4FB698','New York U. Retreat Weekend','31-Oct-2014 23:00:00','02-Sept-2014 16:00:00 UTC','17-Mar-2013 12:29:14','31-Oct-2014 23:00:00 UTC','116');
INSERT INTO conferences (id,conference_costs_id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('40A342D2-0D99-473A-2C3D-7046BFCDD942','40A342D2-0D99-473A-2C3D-7046BFCDD942','Fall Beach Weekend','31-Oct-2014 16:00:00 UTC','2-Nov-2014 09:00:00 UTC','1-Jun-2014 04:00:00 UTC','31-Oct-2014 15:50:00 UTC','289');
INSERT INTO conferences (id,conference_costs_id,name,description,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, location_name, location_address, location_city, location_state, location_zip_code) VALUES
('50A342D2-0D99-473A-2C3D-7046BFCDD942','50A342D2-0D99-473A-2C3D-7046BFCDD942','Winter Beach Weekend!','Training for Missionaries','06-Aug-2013 18:44:48','12-Oct-2013 14:37:10','10-Apr-2013 22:01:21','04-May-2016 19:22:37', '200', 'LifeWay Ridgecrest Conference Center', '1  Ridgecrest Drive', ' Ridgecrest', 'NC', '28770');

