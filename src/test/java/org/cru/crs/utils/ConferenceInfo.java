package org.cru.crs.utils;

import java.util.UUID;

import org.cru.crs.model.ConferenceEntity;

public class ConferenceInfo
{
	public static class Id
	{
		public static UUID NorthernMichigan = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");
	}
	
	public static ConferenceEntity createFakeConference()
	{
		ConferenceEntity conference = new ConferenceEntity();
		
		conference.setId(UUID.randomUUID());
		conference.setName("Fake Conference");
		conference.setDescription("this is a made up conference");
		conference.setConferenceCostsId(conference.getId());
		conference.setContactPersonId(UserInfo.Id.TestUser);
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
		
		
		return conference;
		
		
	}
}
