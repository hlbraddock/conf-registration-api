package org.cru.crs.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.Environment;
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
	
	@BeforeMethod
	private void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
	}
	
	/**
	 * Test: find all the conferences in the system
	 * 
	 * Expected outcome: 10 conferences found
	 * 
	 * Input: none
	 * 
	 * Expected return: 200 - OK and array of 10 json-i-fied conferences
	 */
	@Test(groups="functional-tests")
	public void fetchAllTheConferences()
	{
		ClientResponse<List<Conference>> response = conferenceClient.getConferences();
		
		Assert.assertEquals(response.getStatus(), 200);
		List<Conference> conferences = response.getEntity();
		
		Assert.assertNotNull(conferences);
		Assert.assertEquals(conferences.size(), 10);
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
		
		ClientResponse<Conference> response = conferenceClient.createConference(fakeConference);
		
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
		String conferenceIdString = returnedLocationHeader.substring(resourceFullPathWithoutId.length());
		
		Assert.assertTrue(conferenceReturnedFromCreateCall.getId().toString().equals(conferenceIdString));
		
		/*update our fake conference with the Id generated by the server*/
		fakeConference.setId(UUID.fromString(conferenceIdString));
		
		/*remove the conference from the DB*/
		removeFakeConference(conferenceIdString);
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
		
		ClientResponse<Conference> response = conferenceClient.createConference(fakeConference);
		
		String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
		String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";
		
		/*parse out the conference id from the returned header using the full resource path we expect*/
		UUID conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));
		
		/*update our fake conference with the Id generated by the server*/
		fakeConference.setId(conferenceId);
		
		/*update name value on conference*/
		fakeConference.setName("Updated Fake Fall Retreat");
		
		/*get a fresh client*/
		createClient();
		
		/*call the update endpoint*/
		@SuppressWarnings("rawtypes") 
		ClientResponse updateResponse = conferenceClient.updateConference(fakeConference, conferenceId);
		Assert.assertEquals(updateResponse.getStatus(), 204);
		
		/*get a fresh client*/
		createClient();
		
		ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
		Conference updatedFakeConference = fetchResponse.getEntity();
		
		Assert.assertEquals(updatedFakeConference.getName(), "Updated Fake Fall Retreat");
		
		removeFakeConference(conferenceId.toString());
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
		
		try
		{
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			ClientResponse<Page> response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
			Page pageFromCreatedResponse = response.getEntity();
			
			//status code, 201-Created
			Assert.assertEquals(response.getStatus(), 201);

			/*make sure we get a copy of the page back*/
			Assert.assertNotNull(pageFromCreatedResponse);
			
			/*make sure the ID of the created page matches what was passed in*/
			Assert.assertEquals(pageFromCreatedResponse.getId(), newPage.getId());
			
			ConferenceEntity conference = setupEm.find(ConferenceEntity.class, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));

			Assert.assertEquals(conference.getPages().size(), 2);
			Assert.assertEquals(conference.getPages().get(1).getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
			
			setupEm.close();
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			removeAddedPage(cleanupEm);
			cleanupEm.close();

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
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		
		try
		{
			Page newPage = createFakePage();

			@SuppressWarnings("rawtypes")
			ClientResponse response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb699"));

			//status code, 400-Bad Request
			Assert.assertEquals(response.getStatus(), 400);

			Assert.assertNull(setupEm.find(PageEntity.class, newPage.getId()));
		}
		finally
		{
			setupEm.close();
		}
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
	 */	@Test(groups="functional-tests")
	 public void addPageToConferenceByAddingToAConferenceResourceAndUpdating()
	 {
		UUID testConferenceId = UUID.randomUUID();
		
		try
		{
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			Conference fakeConference = createFakeConference();
			
			fakeConference.setId(testConferenceId);
			
			setupEm.getTransaction().begin();
			setupEm.persist(fakeConference.toJpaConferenceEntity());
			setupEm.flush();
			setupEm.getTransaction().commit();

			setupEm.close();

			fakeConference.getRegistrationPages().add(createFakePage());

			conferenceClient.updateConference(fakeConference, fakeConference.getId());

			EntityManager retrievalEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			ConferenceEntity updatedConference = retrievalEm.find(ConferenceEntity.class, fakeConference.getId());

			Assert.assertEquals(updatedConference.getPages().size(), 1);
			Assert.assertEquals(updatedConference.getPages().get(0).getId(), fakeConference.getRegistrationPages().get(0).getId());
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			cleanupEm.getTransaction().begin();
			cleanupEm.remove(cleanupEm.find(ConferenceEntity.class,testConferenceId));
			cleanupEm.getTransaction().commit();
			cleanupEm.close();
		}
	}
	
	 //**********************************************************************
	 // Helper methods
	 //**********************************************************************

	private void removeFakeConference(String conferenceIdString)
	{
		EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		ConferenceEntity fakeFoundConference = cleanupEm.find(ConferenceEntity.class,UUID.fromString(conferenceIdString));
		
		cleanupEm.getTransaction().begin();
		cleanupEm.remove(fakeFoundConference);
		cleanupEm.flush();
		cleanupEm.getTransaction().commit();
		cleanupEm.close();
	}

	private void removeAddedPage(EntityManager setupEm)
	{
		PageEntity pageToDelete = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		if(pageToDelete == null) return;
		
		setupEm.getTransaction().begin();
		setupEm.remove(pageToDelete);
		setupEm.flush();
		setupEm.getTransaction().commit();
	}
	
	private Conference createFakeConference()
	{
		Conference fakeConference = new Conference();
		fakeConference.setContactUser(UUID.randomUUID());
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
		
		fakePage.setName("Ministry Prefs");
		fakePage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		fakePage.setPosition(1);
		fakePage.setBlocks(null);
		
		return fakePage;
	}

	@Test(groups="functional-tests")
	public void addRegistrationToConference() throws URISyntaxException
	{
		UUID registrationIdUUID = null;
		UUID userIdUUID = UUID.fromString("0a00d62c-af29-3723-f949-95a950a0deaf");

		Registration newRegistration = createRegistration(registrationIdUUID, userIdUUID);

		try
		{
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();

			UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

			ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, conferenceUUID);

			Assert.assertEquals(response.getStatus(), 201);

			registrationIdUUID = getIdFromResponseLocation(response.getLocation().toString());

			RegistrationEntity registration = setupEm.find(RegistrationEntity.class, registrationIdUUID);

			Assert.assertEquals(registration.getId(), registrationIdUUID);

			Assert.assertEquals(registration.getUserId(), newRegistration.getUserId());

			setupEm.close();
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();

			removeAddedRegistration(cleanupEm, registrationIdUUID);

			cleanupEm.close();
		}
	}

	@Test(groups="functional-tests")
	public void getAllConferenceRegistrations()
	{
		UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

		ClientResponse<List<Registration>> response = conferenceClient.getRegistrations(conferenceUUID);

		Assert.assertEquals(response.getStatus(), 200);
		List<Registration> registrations = response.getEntity();

		UUID userIdUUID1 = UUID.fromString("1f6250ca-6d25-2bf4-4e56-f368b2fb8f8a");
		UUID userIdUUID2 = UUID.fromString("7d2201e9-073f-7037-92e0-3b9f7712a8c1");
		UUID userIdUUID3 = UUID.fromString("9c971175-2807-83cc-cb24-ab83433e0e1a");

		Set<UUID> userIdUUIDSet = new HashSet<UUID>();

		userIdUUIDSet.add(userIdUUID1);
		userIdUUIDSet.add(userIdUUID2);
		userIdUUIDSet.add(userIdUUID3);

		Assert.assertNotNull(registrations);
		Assert.assertEquals(registrations.size(), 3);

		for(Registration registration : registrations)
			userIdUUIDSet.remove(registration.getUserId());

		Assert.assertTrue(userIdUUIDSet.size() == 0);
	}

	@Test(groups="functional-tests")
	public void getCurrentRegistration()
	{
		UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

		ClientResponse<Registration> response = conferenceClient.getCurrentRegistration(conferenceUUID);

		Assert.assertEquals(response.getStatus(), 200);

		Registration registration = response.getEntity();

		UUID registrationUUID = UUID.fromString("670a2732-a8b4-4863-b69a-019be680339c");
		UUID userUUID = UUID.fromString("7d2201e9-073f-7037-92e0-3b9f7712a8c1");

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), registrationUUID);
		Assert.assertEquals(registration.getUserId(), userUUID);
		Assert.assertEquals(registration.getConferenceId(), conferenceUUID);
	}

	private Registration createRegistration(UUID registrationIdUUID, UUID userIdUUID)
	{
		Registration registration = new Registration();

		registration.setId(registrationIdUUID);
		registration.setUserId(userIdUUID);

		return registration;
	}

	private void removeAddedRegistration(EntityManager setupEm, UUID registrationIdUUID)
	{
		RegistrationEntity registrationToDelete = setupEm.find(RegistrationEntity.class, registrationIdUUID);
		if(registrationToDelete == null) return;

		setupEm.getTransaction().begin();
		setupEm.remove(registrationToDelete);
		setupEm.flush();
		setupEm.getTransaction().commit();
	}

	private UUID getIdFromResponseLocation(String location)
	{
		location = location.substring(1, location.length()-1);

		return UUID.fromString(location.substring(location.lastIndexOf("/")).substring(1));
	}
}