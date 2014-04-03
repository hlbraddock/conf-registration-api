package org.cru.crs.service;

import java.util.List;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.UserInfo;
import org.sql2o.Connection;
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
	Connection sqlConnection;
	ConferenceService conferenceService;
	
	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		conferenceService = new ConferenceService(sqlConnection,
										new ConferenceCostsService(sqlConnection),
										new PageService(sqlConnection, new BlockService(sqlConnection, new AnswerService(sqlConnection))), 
										new UserService(sqlConnection),
										new PermissionService(sqlConnection));
	}
	
	@Test(groups="dbtest")
	public void testFetchAllConferencesTestUser()
	{		
		List<ConferenceEntity> conferences = conferenceService.fetchAllConferencesForUser(UserInfo.Id.TestUser);
		
		Assert.assertEquals(conferences.size(), 3);
	}
	
	@Test(groups="dbtest")
	public void testFetchAllConferencesRyan()
	{		
		List<ConferenceEntity> conferences = conferenceService.fetchAllConferencesForUser(UserInfo.Id.Ryan);
		
		Assert.assertEquals(conferences.size(), 3);
	}
	
	@Test(groups="dbtest")
	public void testFetchNorthernMichiganConference()
	{		
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(northernMichiganConference);
		
		Assert.assertEquals(northernMichiganConference.getId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(northernMichiganConference.getConferenceCostsId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(northernMichiganConference.getName(), "Northern Michigan Fall Extravaganza");
		Assert.assertEquals(northernMichiganConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 29, 22, 30, 0));
		Assert.assertEquals(northernMichiganConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 31, 16, 0, 0));
		Assert.assertEquals(northernMichiganConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 4, 11, 1, 58, 35));
		Assert.assertEquals(northernMichiganConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2014, 8, 29, 21, 0, 0));
		Assert.assertEquals(northernMichiganConference.getTotalSlots(), 80);
		Assert.assertEquals(northernMichiganConference.getLocationName(), "Black Bear Camp");
		Assert.assertEquals(northernMichiganConference.getLocationAddress(), "5287 St Rt 17");
		Assert.assertEquals(northernMichiganConference.getLocationCity(), "Marquette");
		Assert.assertEquals(northernMichiganConference.getLocationState(), "MI");
		Assert.assertEquals(northernMichiganConference.getLocationZipCode(), "42302");
		Assert.assertFalse(northernMichiganConference.isRequireLogin());
	}
	
	@Test(groups="dbtest")
	public void testCreateNewConference()
	{
		try
		{
			ConferenceEntity conference = ConferenceInfo.createFakeConference();
			ConferenceCostsEntity conferenceCosts = new ConferenceCostsEntity();
			conferenceCosts.setId(conference.getId());
						
			conferenceService.createNewConference(conference, conferenceCosts);
			
			ConferenceEntity retrievedConference = conferenceService.fetchConferenceBy(conference.getId());
			
			Assert.assertNotNull(retrievedConference);
			
			Assert.assertEquals(retrievedConference.getId(), conference.getId());
			Assert.assertEquals(retrievedConference.getName(), conference.getName());
			Assert.assertEquals(retrievedConference.getDescription(), conference.getDescription());
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
			Assert.assertTrue(retrievedConference.isRequireLogin());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest")
	public void testUpdateConferenceNameAndDescription()
	{
		try
		{			
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
	@Test(groups="dbtest")
	public void testUpdateConferenceEventTimes()
	{
		try
		{			
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

    /**
     * This test makes sure that a conference can be deleted.
     */
    @Test(groups="dbtest")
    public void testDeleteConference()
    {
        try
        {
            //Generate a fake conference
            ConferenceEntity conference = ConferenceInfo.createFakeConference();
            ConferenceCostsEntity conferenceCosts = new ConferenceCostsEntity();
            conferenceCosts.setId(conference.getId());

            //Add this conference to the database
            conferenceService.createNewConference(conference, conferenceCosts);

            //Verify that it is in the database
            ConferenceEntity retrievedConference = conferenceService.fetchConferenceBy(conference.getId());
            Assert.assertNotNull(retrievedConference);

            //Delete the conference
            conferenceService.deleteConference(conference.getId());

            //Verify that it is not in the database
            retrievedConference = conferenceService.fetchConferenceBy(conference.getId());
            Assert.assertNull(retrievedConference);
        }
        finally
        {
            sqlConnection.rollback();
        }
    }

    /**
     * This test makes sure that a conference can be archived.
     */
    @Test(groups="dbtest")
    public void testArchiveConference()
    {
        try
        {
            //Generate a fake conference
            ConferenceEntity conference = ConferenceInfo.createFakeConference();
            ConferenceCostsEntity conferenceCosts = new ConferenceCostsEntity();
            conferenceCosts.setId(conference.getId());

            //Add this conference to the database
            conferenceService.createNewConference(conference, conferenceCosts);

            //Verify that it is in the database
            ConferenceEntity retrievedConference = conferenceService.fetchConferenceBy(conference.getId());
            Assert.assertNotNull(retrievedConference);

            //Archive the conference
            conference.setArchived(true);
            conferenceService.updateConference(conference);

            //Verify that it is archived in the database
            retrievedConference = conferenceService.fetchConferenceBy(conference.getId());
            Assert.assertEquals(retrievedConference.isArchived(), true);
        }
        finally
        {
            sqlConnection.rollback();
        }
    }

}
