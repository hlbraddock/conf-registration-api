package org.cru.crs.utils;

import org.cru.crs.model.ConferenceEntity;

import java.util.UUID;

public class ConferenceInfo
{
	public static class Id
	{
		public static UUID NorthernMichigan = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");
		public static UUID MiamiUniversity = UUID.fromString("1951613E-A253-1AF8-6BC4-C9F1D0B3FA60");
		public static UUID NewYork = UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698");
		public static UUID FallBeach = UUID.fromString("40A342D2-0D99-473A-2C3D-7046BFCDD942");
		public static UUID WinterBeachCold = UUID.fromString("50A342D2-0D99-473A-2C3D-7046BFCDD942");
	}
	
	public static ConferenceEntity createFakeConference()
	{
		ConferenceEntity conference = new ConferenceEntity();
		
		conference.setId(UUID.randomUUID());
		conference.setName("Fake Conference");
		conference.setDescription("this is a made up conference");
		conference.setConferenceCostsId(conference.getId());
		conference.setContactPersonName("Test User");
		conference.setContactPersonEmail("crs.testuser@crue.org");
		conference.setContactPersonPhone("555/555-5555");
		conference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2014, 9, 1, 15, 0, 0));
		conference.setEventEndTime(DateTimeCreaterHelper.createDateTime(2014, 9, 5, 13, 30, 0));
		conference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2014, 7, 2, 8, 30, 0));
		conference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2014, 9, 4, 23, 59, 59));
		conference.setLocationAddress("123 Home St.");
		conference.setLocationCity("Bowling Green");
		conference.setLocationState("OH");
		conference.setLocationZipCode("43402");
		conference.setTotalSlots(172);
		conference.setRequireLogin(true);
        conference.setArchived(false);
		
		return conference;
	}
}
