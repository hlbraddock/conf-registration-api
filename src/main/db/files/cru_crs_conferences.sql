DROP TABLE cru_crs_conferences;

CREATE TABLE cru_crs_conferences (
  id uuid NOT NULL PRIMARY KEY,
  name text,
  event_start_time timestamp with time zone,
  event_end_time timestamp with time zone,
  registration_start_time timestamp with time zone,
  registration_end_time timestamp with time zone,
  total_slots integer default NULL,
  contact_user uuid
);

INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, contact_user) VALUES ('42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309','Northern Michigan Fall Extravaganza','24-Aug-2013 10:32:08','02-Oct-2013 02:43:14','10-Apr-2013 21:58:35','22-May-2013 18:53:08','80','f8f8c217-f918-4503-b3b3-85016f9883c1');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('1951613E-A253-1AF8-6BC4-C9F1D0B3FA60','Miami University Fall Retreat','09-Aug-2013 01:22:28','25-Sep-2013 03:53:34','08-Mar-2013 00:51:50','12-May-2013 10:17:08','197');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('D5878EBA-9B3F-7F33-8355-3193BF4FB698','New York U. Retreat Weekend','20-Aug-2013 22:49:51','30-Sep-2013 13:58:19','17-Mar-2013 12:29:14','13-May-2013 08:09:37','116');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, contact_user) VALUES ('1CFA829F-2C3A-F803-A966-9A6510EE2F33','NC State Wolfpack Camp','23-Aug-2013 05:17:55','22-Sep-2013 02:12:46','11-Apr-2013 00:56:55','05-May-2013 18:34:29','95','f8f8c217-f918-4503-b3b3-85016f9883c1');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots, contact_user) VALUES ('67384634-F379-DE07-86DA-6B89ABC6FAB5','Atlanta Metro Area Fall Camp','12-Aug-2013 03:15:00','13-Sep-2013 08:58:54','04-Mar-2013 04:09:13','10-May-2013 22:48:58','140','f8f8c217-f918-4503-b3b3-85016f9883c1');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('40A342D2-0D99-473A-2C3D-7046BFCDD942','Fall Beach Weekend','06-Aug-2013 18:44:48','12-Oct-2013 14:37:10','10-Apr-2013 22:01:21','04-May-2013 19:22:37','289');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('23120BC5-2005-C434-47B0-77E5137C2DB5','BGSU BiG Fall Getaway','01-Aug-2013 00:53:41','19-Oct-2013 19:19:45','04-Apr-2013 16:44:55','24-May-2013 03:52:15','56');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('847B4710-B450-C003-7F99-2055E0AFD206','UCF Fall Retreat','21-Aug-2013 13:27:07','07-Sep-2013 16:53:18','04-Apr-2013 03:15:33','14-May-2013 20:56:19','218');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('F3CA81A4-1B89-A12C-2710-8B06E1C49FF8','Chicago Area Fall Getaway','08-Aug-2013 00:30:06','17-Oct-2013 15:37:10','09-Mar-2013 01:45:06','18-May-2013 23:44:08','212');
INSERT INTO cru_crs_conferences (id,name,event_start_time,event_end_time,registration_start_time,registration_end_time,total_slots) VALUES ('DDFE8883-9AFC-21DB-DD1B-EE08D000C3B9','Ohio University Fall Retreat','12-Aug-2013 21:26:43','01-Sep-2013 06:36:51','18-Apr-2013 21:24:32','06-May-2013 22:53:58','194');