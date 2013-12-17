package org.cru.crs.service;

import java.util.List;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * These tests are meant to be run on a fresh database.
 * 
 * @author ryancarlson
 *
 */
public class ConferenceServiceTest
{

	org.sql2o.Connection sqlConnection;
	
	@BeforeMethod
	private ConferenceService getConferenceService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		return new ConferenceService(sqlConnection,
										new ConferenceCostsService(sqlConnection),
										new PageService(sqlConnection, new BlockService(sqlConnection, new AnswerService(sqlConnection))), new UserService(sqlConnection));
	}
	
	@Test
	public void testFetchAllConferencesTestUser()
	{
		ConferenceService conferenceService = getConferenceService();
		
		List<ConferenceEntity> conferences = conferenceService.fetchAllConferences(UserInfo.Users.TestUser);
		
		Assert.assertEquals(conferences.size(), 2);
	}
	
	@Test
	public void testFetchAllConferencesRyan()
	{
		ConferenceService conferenceService = getConferenceService();
		
		List<ConferenceEntity> conferences = conferenceService.fetchAllConferences(UserInfo.Users.Ryan);
		
		Assert.assertEquals(conferences.size(), 2);
	}
	
	@Test
	public void testFetchNorthernMichiganConference()
	{
		ConferenceService conferenceService = getConferenceService();
		
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(northernMichiganConference);
		
		Assert.assertEquals(northernMichiganConference.getId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(northernMichiganConference.getConferenceCostsId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(northernMichiganConference.getName(), "Northern Michigan Fall Extravaganza");
		Assert.assertEquals(northernMichiganConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 24, 10, 32, 8));
		Assert.assertEquals(northernMichiganConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2014, 10, 2, 2, 43, 14));
		Assert.assertEquals(northernMichiganConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 4, 10, 21, 58, 35));
		Assert.assertEquals(northernMichiganConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013, 12, 22, 18, 53, 8));
		Assert.assertEquals(northernMichiganConference.getTotalSlots(), 80);
		Assert.assertEquals(northernMichiganConference.getContactPersonId(), UserInfo.Id.TestUser);
		Assert.assertEquals(northernMichiganConference.getLocationName(), "Black Bear Camp");
		Assert.assertEquals(northernMichiganConference.getLocationAddress(), "5287 St Rt 17");
		Assert.assertEquals(northernMichiganConference.getLocationCity(), "Marquette");
		Assert.assertEquals(northernMichiganConference.getLocationState(), "MI");
		Assert.assertEquals(northernMichiganConference.getLocationZipCode(), "42302");
	}
	
	@Test
	public void testCreateNewConference()
	{
		try
		{
			ConferenceEntity conference = ConferenceInfo.createFakeConference();
			ConferenceCostsEntity conferenceCosts = new ConferenceCostsEntity();
			conferenceCosts.setId(conference.getId());
			
			ConferenceService conferenceService = getConferenceService();
			
			conferenceService.createNewConference(conference, conferenceCosts);
			
			ConferenceEntity retrievedConference = conferenceService.fetchConferenceBy(conference.getId());
			
			Assert.assertNotNull(retrievedConference);
			
			Assert.assertEquals(retrievedConference.getId(), conference.getId());
			Assert.assertEquals(retrievedConference.getName(), conference.getName());
			Assert.assertEquals(retrievedConference.getDescription(), conference.getDescription());
			Assert.assertEquals(retrievedConference.getContactPersonId(), conference.getContactPersonId());
			Assert.assertEquals(retrievedConference.getContactPersonEmail(), conference.getContactPersonEmail());
			Assert.assertEquals(retrievedConference.getContactPersonName(), conference.getContactPersonName());
			Assert.assertEquals(retrievedConference.getContactPersonPhone(), conference.getContactPersonPhone());
			Assert.assertEquals(retrievedConference.getLocationAddress(), conference.getLocationAddress());
			Assert.assertEquals(retrievedConference.getLocationCity(), conference.getLocationCity());
			Assert.assertEquals(retrievedConference.getLocationState(), conference.getLocationState());
			Assert.assertEquals(retrievedConference.getLocationZipCode(), conference.getLocationZipCode());
			Assert.assertEquals(retrievedConference.getConferenceCostsId(), conference.getConferenceCostsId());
			Assert.assertEquals(retrievedConference.getRegistrationStartTime(), conference.getRegistrationStartTime());
			Assert.assertEquals(retrievedConference.getRegistrationEndTime(), conference.getRegistrationEndTime());
			Assert.assertEquals(retrievedConference.getEventStartTime(), conference.getEventStartTime());
			Assert.assertEquals(retrievedConference.getEventEndTime(), conference.getEventEndTime());
			Assert.assertEquals(retrievedConference.getTotalSlots(), conference.getTotalSlots());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testUpdateConferenceNameAndDescription()
	{
		try
		{
			ConferenceService conferenceService = getConferenceService();
			
			ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);

			Assert.assertNotEquals(northernMichiganConference.getName(), "Northern Michigan what's a conference?");
			Assert.assertNotEquals(northernMichiganConference.getDescription(), "Someone is running a test...");

			northernMichiganConference.setName("Northern Michigan what's a conference?");
			northernMichiganConference.setDescription("Someone is running a test...");
			
			conferenceService.updateConference(northernMichiganConference);
			
			ConferenceEntity updatedConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
			
			Assert.assertEquals(updatedConference.getId(), northernMichiganConference.getId());
			Assert.assertEquals(updatedConference.getName(), "Northern Michigan what's a conference?");
			Assert.assertEquals(updatedConference.getDescription(), "Someone is running a test...");
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	/**
	 * This test makes sure that updating date/time/timestamps works correctly.
	 */
	@Test
	public void testUpdateConferenceEventTimes()
	{
		try
		{
			ConferenceService conferenceService = getConferenceService();
			
			ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);

			Assert.assertNotEquals(northernMichiganConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2014, 10, 2, 16, 15, 22));
			Assert.assertNotEquals(northernMichiganConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2014, 10, 6, 5, 33, 2));

			northernMichiganConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2014, 10, 2, 16, 15, 22));
			northernMichiganConference.setEventEndTime(DateTimeCreaterHelper.createDateTime(2014, 10, 6, 5, 33, 2));
			
			conferenceService.updateConference(northernMichiganConference);
			
			ConferenceEntity updatedConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
			
			Assert.assertEquals(updatedConference.getId(), northernMichiganConference.getId());
			Assert.assertEquals(updatedConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2014, 10, 2, 16, 15, 22));
			Assert.assertEquals(updatedConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2014, 10, 6, 5, 33, 2));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
