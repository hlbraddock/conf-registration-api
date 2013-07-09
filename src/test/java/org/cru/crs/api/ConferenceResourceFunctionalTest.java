package org.cru.crs.api;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
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
 *
 */
@Test(groups="functional-tests")
public class ConferenceResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	
	Environment environment = Environment.LOCAL;
	ConferenceResourceClient conferenceClient;
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
	}
	
	@Test(groups="functional-tests")
	public void fetchAllTheConferences()
	{
		ClientResponse<List<ConferenceEntity>> response = conferenceClient.getConferences();
		
		Assert.assertEquals(response.getStatus(), 200);
		List<ConferenceEntity> conferences = response.getEntity();
		
		Assert.assertNotNull(conferences);
		Assert.assertEquals(conferences.size(), 10);
	}
	
	@Test(groups="functional-tests")
	public void fetchConferenceById()
	{
		ClientResponse<ConferenceEntity> response = conferenceClient.getConference(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
		
		Assert.assertEquals(response.getStatus(), 200);
		ConferenceEntity conference = response.getEntity();
		
		Assert.assertNotNull(conference);
		Assert.assertEquals(conference.getName(), "New York U. Retreat Weekend");
		Assert.assertEquals(conference.getTotalSlots(), 116);
	}
	
	/**
	 * TODO: method needs to clean up after itself
	 * @throws URISyntaxException
	 */
	@Test(groups="functional-tests")
	public void createConference() throws URISyntaxException
	{
		ConferenceEntity fakeConference = createFakeConference();
		ClientResponse<ConferenceEntity> response = conferenceClient.createConference(fakeConference);
		
		String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
		String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";
		
		/*status should be 201 - CREATED*/
		Assert.assertEquals(response.getStatus(), 201);
		
		/*make sure the returned location has the resource path we expect*/
		Assert.assertTrue(returnedLocationHeader.contains(resourceFullPathWithoutId));
		
		/*parse out the conference id from the returned header using the full resource path we expect*/
		String conferenceIdString = returnedLocationHeader.substring(resourceFullPathWithoutId.length());
		
		/*update our fake conference with the Id generated by the server*/
		fakeConference.setId(UUID.fromString(conferenceIdString));
		
		/*remove the conference from the DB*/
		removeFakeConference(conferenceIdString);
	}

	@Test(groups="functional-tests")
	public void updateConference() throws URISyntaxException
	{
		ConferenceEntity fakeConference = createFakeConference();
		ClientResponse<ConferenceEntity> response = conferenceClient.createConference(fakeConference);
		
		String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
		String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";
		
		/*parse out the conference id from the returned header using the full resource path we expect*/
		String conferenceIdString = returnedLocationHeader.substring(resourceFullPathWithoutId.length());
		
		/*update our fake conference with the Id generated by the server*/
		fakeConference.setId(UUID.fromString(conferenceIdString));
		
		/*update name value on conference*/
		fakeConference.setName("Updated Fake Fall Retreat");
		
		/*get a fresh client*/
		createClient();
		
		/*call the update endpoint*/
		ClientResponse<ConferenceEntity> updateResponse = conferenceClient.updateConference(fakeConference, conferenceIdString);
		Assert.assertEquals(updateResponse.getStatus(), 204);
		
		/*get a fresh client*/
		createClient();
		
		ClientResponse<ConferenceEntity> fetchResponse = conferenceClient.getConference(fakeConference.getId());
		ConferenceEntity updatedFakeConference = fetchResponse.getEntity();
		
		Assert.assertEquals(updatedFakeConference.getName(), "Updated Fake Fall Retreat");
		
		removeFakeConference(conferenceIdString);
	}
	
	@Test(groups="functional-tests")
	public void addPageToConference() throws URISyntaxException 
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		
		//do this pre-check b/c JPA doesn't always do the delete correctly.  the last test might not have
		//cleaned up after itself
		removeAddedPage(setupEm);
		
		PageEntity newPage = createFakePage();
		
		try
		{
			ClientResponse<ConferenceEntity> response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));

			//status code, 201-Created
			Assert.assertEquals(response.getStatus(), 201);

			ConferenceEntity conference = setupEm.find(ConferenceEntity.class, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));

			Assert.assertEquals(conference.getPages().size(), 2);
			Assert.assertEquals(conference.getPages().get(1).getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		}
		finally
		{
			removeAddedPage(setupEm);
			setupEm.close();

		}
	}

	@Test(groups="functional-tests")
	public void addPageToBogusConference() throws URISyntaxException
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		
		try
		{
			//do this pre-check b/c JPA doesn't always do the delete correctly.  the last test might not have
			//cleaned up after itself
			removeAddedPage(setupEm);

			PageEntity newPage = createFakePage();
			newPage.setConferenceId(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb699"));

			ClientResponse<ConferenceEntity> response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb699"));

			//status code, 400-Bad Request
			Assert.assertEquals(response.getStatus(), 400);

			Assert.assertNull(setupEm.find(PageEntity.class, newPage.getId()));
		}
		finally
		{
			setupEm.close();
		}
	}
	
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
	
	private ConferenceEntity createFakeConference()
	{
		ConferenceEntity fakeConference = new ConferenceEntity();
		fakeConference.setContactUser(UUID.randomUUID());
		fakeConference.setName("Fake Fall Retreat");
		fakeConference.setTotalSlots(202);
		fakeConference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 6, 1, 8, 0, 0));
		fakeConference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 6, 22, 23, 59, 59));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 4, 15, 0, 0));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 9, 11, 0, 0));
		
		return fakeConference;
	}
	
	private PageEntity createFakePage()
	{
		PageEntity fakePage = new PageEntity();
		
		fakePage.setName("Ministry Prefs");
		fakePage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		fakePage.setConferenceId(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa60"));
		fakePage.setPosition(1);
		fakePage.setBlocks(null);
		
		return fakePage;
	}
}