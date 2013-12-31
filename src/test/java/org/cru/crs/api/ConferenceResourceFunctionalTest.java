package org.cru.crs.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.api.model.Block;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.ConferenceFetchProcess;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test requires an EE app server to be running to test the endpoint/resource
 * @author ryancarlson
 */
@Test(groups="functional-tests")
public class ConferenceResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;

	ConferenceResourceClient conferenceClient;
	
	org.sql2o.Connection sqlConnection;
	
	ConferenceService conferenceService;
	PaymentService paymentService;
	PageService pageService;
	RegistrationService registrationService;
	
	ConferenceFetchProcess conferenceFetchProcess;

	@BeforeMethod
	private void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
        
        sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
        
        AnswerService answerService = new AnswerService(sqlConnection);
        BlockService blockService = new BlockService(sqlConnection, answerService);
        pageService = new PageService(sqlConnection,blockService);
        ConferenceCostsService conferenceCostsService = new ConferenceCostsService(sqlConnection);
        conferenceService = new ConferenceService(sqlConnection, conferenceCostsService, pageService, new UserService(sqlConnection));
        paymentService = new PaymentService(sqlConnection);
        registrationService = new RegistrationService(sqlConnection, answerService,paymentService);
        
        conferenceFetchProcess = new ConferenceFetchProcess(conferenceService, conferenceCostsService, pageService, blockService, new ClockImpl());
	}
	
	/**
	 * Test: find all the conferences in the system
	 * 
	 * Expected outcome: 0 conferences found
	 * 
	 * Input: none
	 * 
	 * Expected return: 200 - OK and empty array of json-i-fied conferences, b/c this list is now limited
	 * by the authenticated user in the session
	 */
	@Test(groups="functional-tests")
	public void fetchAllTheConferences()
	{
		ClientResponse<List<Conference>> response = conferenceClient.getConferences(UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);
		List<Conference> conferences = response.getEntity();
		
		Assert.assertNotNull(conferences);
		Assert.assertEquals(conferences.size(), 2);
		
		for(Conference conference : conferences)
		{
			//the two conferences should be these two.
			if(!("Northern Michigan Fall Extravaganza".equals(conference.getName()) ||
					"Miami University Fall Retreat".equals(conference.getName())))
			{
				Assert.fail();
			}
		}
	}
	
	/**
	 * Test: find conference by id
	 * 
	 * Expected outcome: conference found and returned as json w/ associated pages and blocks
	 * 
	 * Input: UUID - d5878eba-9b3f-7f33-8355-3193bf4fb698
	 * 
	 * Expected return: 200 - OK and conference JSON resource specified by ID
	 */
	@Test(groups="functional-tests")
	public void fetchConferenceById()
	{
		ClientResponse<Conference> response = conferenceClient.getConference(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
		
		Assert.assertEquals(response.getStatus(), 200);
		Conference conference = response.getEntity();
		
		Assert.assertNotNull(conference);
		Assert.assertEquals(conference.getName(), "New York U. Retreat Weekend");
		Assert.assertEquals(conference.getTotalSlots(), 116);
	}
	
	/**
	 * Test: create a new conference
	 * 
	 * Expected outcome: conference created and location header of new conference is returned
	 * 
	 * Input: conference JSON resource
	 * 
	 * Expected return: 201 - Created and location header of new conference resource
	 */
	@Test(groups="functional-tests")
	public void createConference() throws URISyntaxException
	{
		Conference fakeConference = createFakeConference();
		String conferenceIdString = null;
		try
		{
			ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";
			Conference conferenceReturnedFromCreateCall = response.getEntity();

			/*status should be 201 - CREATED*/
			Assert.assertEquals(response.getStatus(), 201);

			/*make sure we get a copy of the conference back*/
			Assert.assertNotNull(conferenceReturnedFromCreateCall);

			/*make sure the returned location has the resource path we expect*/
			Assert.assertTrue(returnedLocationHeader.contains(resourceFullPathWithoutId));

			/*parse out the conference id from the returned header using the full resource path we expect*/
			conferenceIdString = returnedLocationHeader.substring(resourceFullPathWithoutId.length());

			Assert.assertTrue(conferenceReturnedFromCreateCall.getId().toString().equals(conferenceIdString));

			/*update our fake conference with the Id generated by the server*/
			fakeConference.setId(UUID.fromString(conferenceIdString));
		}
		finally
		{
			if(conferenceIdString != null) deleteConferenceForNextTests(UUID.fromString(conferenceIdString));
			sqlConnection.commit();
		}
	}

	
	/**
	 * Test: update existing conference by changing name
	 * 
	 * Expected outcome: conference after update can be retrieved and new name is verified
	 * 
	 * Input: Updated conference object
	 * 
	 * Expected return: 204 - No Content
	 */
	@Test(groups="functional-tests")
	public void updateConference() throws URISyntaxException
	{
		Conference fakeConference = createFakeConference();
		UUID conferenceId = null;
		
		try
		{
			ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";

			/*parse out the conference id from the returned header using the full resource path we expect*/
			conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			/*update our fake conference with the Id generated by the server*/
			fakeConference.setId(conferenceId);

			/*update name value on conference*/
			fakeConference.setName("Updated Fake Fall Retreat");

			/*get a fresh client*/
			createClient();

			/*call the update endpoint*/
			@SuppressWarnings("rawtypes") 
			ClientResponse updateResponse = conferenceClient.updateConference(fakeConference, conferenceId, UserInfo.AuthCode.TestUser);
			Assert.assertEquals(updateResponse.getStatus(), 204);

			/*get a fresh client*/
			createClient();

			ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
			Conference updatedFakeConference = fetchResponse.getEntity();

			Assert.assertEquals(updatedFakeConference.getName(), "Updated Fake Fall Retreat");
		}
		finally
		{
			deleteConferenceForNextTests(conferenceId);
			sqlConnection.commit();
		}
	}

	/**
	 * Test: add a new page to an existing conference
	 * 
	 * Expected outcome: existing conference will have a new page added to it
	 * 
	 * Input: new page JSON resource and conference UUID - d5878eba-9b3f-7f33-8355-3193bf4fb698
	 * 
	 * Expected return: 201 - Created and location header to newly created page
	 */
	@Test(groups="functional-tests")
	public void addPageToConference() throws URISyntaxException 
	{
		Page newPage = createFakePage();
		UUID pageId = null;
		try
		{
			ClientResponse<Page> response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"), UserInfo.AuthCode.Ryan);
			Page pageFromCreatedResponse = response.getEntity();

			//status code, 201-Created
			Assert.assertEquals(response.getStatus(), 201);

			/*make sure we get a copy of the page back*/
			Assert.assertNotNull(pageFromCreatedResponse);
			pageId = pageFromCreatedResponse.getId();
			/*make sure the ID of the created page matches what was passed in*/
			Assert.assertEquals(pageFromCreatedResponse.getId(), newPage.getId());

			List<PageEntity> pages = pageService.fetchPagesForConference(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
			Assert.assertEquals(pages.size(), 3);
			Assert.assertEquals(pages.get(2).getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		}
		finally
		{
			deletePageForNextTests(pageId);
			sqlConnection.commit();
		}
	}

	/**
	 * Test: add a page to conference that doesn't exist
	 * 
	 * Expected outcome: should fail.. cannot add a page to non-existant conference
	 * 
	 * Input: JSON page resource and UUID - d5878eba-9b3f-7f33-8355-3193bf4fb699
	 * 
	 * Expected return: 400 - Bad Request
	 */
	@Test(groups="functional-tests")
	public void addPageToBogusConference() throws URISyntaxException
	{
		Page newPage = createFakePage();

		ClientResponse response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb699"), UserInfo.AuthCode.TestUser);

		//status code, 400-Bad Request
		Assert.assertEquals(response.getStatus(), 400);
	}

	@Test(groups="functional-tests")
	public void addRegistrationToConference() throws URISyntaxException
	{
		UUID registrationId = null;
		try
		{
			UUID userIdUUID = UserInfo.Id.Ryan;

			Registration newRegistration = createRegistration(registrationId, userIdUUID);

			UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

			ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, conferenceUUID, UserInfo.AuthCode.Ryan);

			Assert.assertEquals(response.getStatus(), 201);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/pages/";
			registrationId =  UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			RegistrationEntity registration = registrationService.getRegistrationBy(registrationId);
			List<PaymentEntity> payments = paymentService.fetchPaymentsForRegistration(registrationId);

			Assert.assertEquals(registration.getId(), registrationId);
			Assert.assertEquals(registration.getUserId(), newRegistration.getUserId());
			Assert.assertEquals(payments.size(), 0);
		}
		finally
		{
			deleteRegistrationForNextTests(registrationId);
			sqlConnection.commit();
		}

	}

	@Test(groups="functional-tests")
	public void getAllConferenceRegistrations()
	{
		UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

		ClientResponse<List<Registration>> response = conferenceClient.getRegistrations(conferenceUUID, UserInfo.AuthCode.TestUser);

		Assert.assertEquals(response.getStatus(), 200);
		List<Registration> registrations = response.getEntity();

		Set<UUID> userIdUUIDSet = new HashSet<UUID>();

		userIdUUIDSet.add(UserInfo.Id.Email);
		userIdUUIDSet.add(UserInfo.Id.TestUser);

		Assert.assertNotNull(registrations);
		Assert.assertEquals(registrations.size(), 2);

		for(Registration registration : registrations)
		{
			userIdUUIDSet.remove(registration.getUserId());
		}

		Assert.assertTrue(userIdUUIDSet.size() == 0);
	}

	@Test(groups="functional-tests")
	public void getCurrentRegistration()
	{
		UUID conferenceUUID = UUID.fromString("1951613E-A253-1AF8-6BC4-C9F1D0B3FA60");

		ClientResponse<Registration> response = conferenceClient.getCurrentRegistration(conferenceUUID, UserInfo.AuthCode.Ryan);

		Assert.assertEquals(response.getStatus(), 200);

		Registration registration = response.getEntity();

		UUID registrationUUID = UUID.fromString("AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111");

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), registrationUUID);
		Assert.assertEquals(registration.getUserId(), UserInfo.Id.Ryan);
		Assert.assertEquals(registration.getConferenceId(), conferenceUUID);
		Assert.assertNull(registration.getCurrentPayment());
		Assert.assertNotNull(registration.getPastPayments());
		Assert.assertFalse(registration.getPastPayments().isEmpty());
	}
	
	/**
	 * Test: add a page to conference by fetching the conference and adding a child page to it. this is different than hitting
	 * the createPage endpoint, but should have the same outcome
	 * 
	 * Expected outcome: page should be stored and associated with conference
	 * 
	 * Input: JSON conference resource with new page attached to it
	 * 
	 * Expected return: 204 - No Content
	 */	
	@Test(groups="functional-tests")
	 public void addPageToConferenceByAddingToAConferenceResourceAndUpdating() throws Exception
	 {
		UUID testConferenceId = UUID.randomUUID();
		UUID testPageId = null;
		
		try
		{
			Conference fakeConference = createFakeConference();	
			fakeConference.setId(testConferenceId);

			conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			fakeConference.getRegistrationPages().add(createFakePage());

			createClient();

			ClientResponse<Conference> updateResponse = conferenceClient.updateConference(fakeConference, fakeConference.getId(), UserInfo.AuthCode.TestUser);

			Assert.assertEquals(updateResponse.getStatus(), 204);

			createClient();

			ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
			Conference updatedConference = fetchResponse.getEntity();

			Assert.assertNotNull(updatedConference.getRegistrationPages().get(0));
			testPageId = updatedConference.getRegistrationPages().get(0).getId();
		}
		finally
		{
			deletePageForNextTests(testPageId);
			deleteConferenceForNextTests(testConferenceId);
			sqlConnection.commit();
		}
	 }
	
	@Test(groups="functional-tests")
	public void moveBlockUpOnePage()
	{
		moveBlock(1, 0, 1, UUID.fromString("D5878EBA-9B3F-7F33-8355-3193BF4FB698"));
	}

	@Test(groups="functional-tests")
	public void moveBlockDownOnePage()
	{
		moveBlock(0, 1, 1, UUID.fromString("D5878EBA-9B3F-7F33-8355-3193BF4FB698"));
	}

	private void moveBlock(int sourcePageIndex, int destinationPageIndex, int blockToRemoveIndex, UUID conferenceUUID)
	{
		// retrieve conference & pages
		Conference webConference = conferenceFetchProcess.get(conferenceUUID);

		List<Block> sourceBlocks = webConference.getRegistrationPages().get(sourcePageIndex).getBlocks();
		List<Block> destinationBlocks = webConference.getRegistrationPages().get(destinationPageIndex).getBlocks();

		// record original block size
		int sourceBlocksSize = sourceBlocks.size();
		int destinationBlocksSize = destinationBlocks.size();

		// move block from source to destination page
		Block blockToMove = sourceBlocks.remove(blockToRemoveIndex);
		destinationBlocks.add(blockToMove);

		// update conference pages
		ClientResponse updateResponse = conferenceClient.updateConference(webConference, webConference.getId(), UserInfo.AuthCode.Ryan);
		Assert.assertEquals(updateResponse.getStatus(), 204);

		// retrieve conference & pages
		Conference updatedWebConference = conferenceFetchProcess.get(conferenceUUID);

		List<Block> updatedSourceBlocks = updatedWebConference.getRegistrationPages().get(sourcePageIndex).getBlocks();
		List<Block> updatedDestinationBlocks = updatedWebConference.getRegistrationPages().get(destinationPageIndex).getBlocks();

		// ensure block moved
		Assert.assertEquals(updatedSourceBlocks.size(), sourceBlocksSize-1);
		Assert.assertEquals(updatedDestinationBlocks.size(), destinationBlocksSize+1);
		Assert.assertTrue(updatedDestinationBlocks.contains(blockToMove));
		Assert.assertEquals(updatedDestinationBlocks.get(updatedDestinationBlocks.size()-1), blockToMove);

		// restore block to original page
		updatedDestinationBlocks.remove(blockToMove);
		updatedSourceBlocks.add(blockToMove);

		conferenceClient.updateConference(updatedWebConference, webConference.getId(), UserInfo.AuthCode.Ryan);
	}

	//**********************************************************************
	 // Helper methods
	 //**********************************************************************
	
	private Conference createFakeConference()
	{
		Conference fakeConference = new Conference();
		fakeConference.setContactUser(UserInfo.Id.TestUser);
		fakeConference.setName("Fake Fall Retreat");
		fakeConference.setTotalSlots(202);
		fakeConference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 6, 1, 8, 0, 0));
		fakeConference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 6, 22, 23, 59, 59));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 4, 15, 0, 0));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 9, 11, 0, 0));
		fakeConference.setRegistrationPages(new ArrayList<Page>());
		
		return fakeConference;
	}
	
	private Page createFakePage()
	{
		Page fakePage = new Page();
		
		fakePage.setTitle("Ministry Prefs");
		fakePage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		fakePage.setPosition(1);
		fakePage.setBlocks(null);
		
		return fakePage;
	}

	private Registration createRegistration(UUID registrationIdUUID, UUID userIdUUID)
	{
		Registration registration = new Registration();

		registration.setId(registrationIdUUID);
		registration.setUserId(userIdUUID);

		return registration;
	}
	
	private void deleteConferenceForNextTests(UUID conferenceId)
	{
		if(conferenceId != null)
		{
			sqlConnection.createQuery("DELETE FROM conferences WHERE id = :id")
								.addParameter("id", conferenceId)
								.executeUpdate();
		}
	}
	
	private void deletePageForNextTests(UUID pageId)
	{
		if(pageId != null)
		{
			sqlConnection.createQuery("DELETE FROM pages WHERE id = :id")
							.addParameter("id", pageId)
							.executeUpdate();
		}
	}
	
	private void deleteRegistrationForNextTests(UUID registrationId)
	{
		if(registrationId != null)
		{
			sqlConnection.createQuery("DELETE FROM registrations WHERE id = :id")
								.addParameter("id", registrationId)
								.executeUpdate();
		}
	}
}