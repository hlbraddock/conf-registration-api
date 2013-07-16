package org.cru.crs.api.model;

import java.util.UUID;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConferenceDataTransferTest
{

	@Test(groups="unittest")
	public void testWebEntityToJpaEntity()
	{
		Conference conference = new Conference();
		
		conference.setContactUser(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		conference.setEventEndTime(DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		conference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		conference.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		conference.setName("Awesome fall retreat");
		conference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		conference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		conference.setTotalSlots(150);
		
		ConferenceEntity jpaConference = conference.toJpaConferenceEntity();
		
		Assert.assertNotNull(jpaConference);
		Assert.assertEquals(jpaConference.getContactUser(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		Assert.assertEquals(jpaConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		Assert.assertEquals(jpaConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		Assert.assertEquals(jpaConference.getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		Assert.assertEquals(jpaConference.getName(), "Awesome fall retreat");
		Assert.assertEquals(jpaConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		Assert.assertEquals(jpaConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		Assert.assertEquals(jpaConference.getTotalSlots(), 150);
	}
	
	@Test(groups="unittest")
	public void testJpaEntityToWebEntity()
	{
		ConferenceEntity conference = new ConferenceEntity();
		
		conference.setContactUser(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		conference.setEventEndTime(DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		conference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		conference.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		conference.setName("Awesome fall retreat");
		conference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		conference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		conference.setTotalSlots(150);
		
		Conference webConference = Conference.fromJpa(conference);
		
		Assert.assertNotNull(webConference);
		Assert.assertEquals(webConference.getContactUser(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		Assert.assertEquals(webConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		Assert.assertEquals(webConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		Assert.assertEquals(webConference.getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		Assert.assertEquals(webConference.getName(), "Awesome fall retreat");
		Assert.assertEquals(webConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		Assert.assertEquals(webConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		Assert.assertEquals(webConference.getTotalSlots(), 150);
	}
}
