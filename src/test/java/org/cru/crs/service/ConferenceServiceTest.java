package org.cru.crs.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups="db-integration-tests")
public class ConferenceServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	private EntityManagerFactory emFactory;
	private EntityManager em;
	
	private ConferenceService conferenceService;
	
	private CrsApplicationUser testAppUser = new CrsApplicationUser(UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898"), AuthenticationProviderType.RELAY, "crs.testuser@crue.org");
	private CrsApplicationUser testAppUserNotAuthorized = new CrsApplicationUser(UUID.randomUUID(), null, null);
		
	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		conferenceService = new ConferenceService(em);
	}
	
	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}
	
	@Test(groups="db-integration-tests")
	public void fetchAllTheConferences()
	{
		List<ConferenceEntity> allConferences = conferenceService.fetchAllConferences(testAppUser);
		
		Assert.assertNotNull(allConferences);
		Assert.assertFalse(allConferences.isEmpty());
		Assert.assertEquals(allConferences.size(),2);
		
		for(ConferenceEntity conference : allConferences)
		{
			Assert.assertNotNull(conference.getName());
			Assert.assertNotNull(conference.getId());
		}
	}
	
	@Test(groups="db-integration-tests")
	public void fetchConferenceById()
	{
		ConferenceEntity conference = conferenceService.fetchConferenceBy(java.util.UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		
		Assert.assertNotNull(conference);
		
		Assert.assertEquals(conference.getId(), UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		Assert.assertEquals(conference.getName(), "Northern Michigan Fall Extravaganza");
		Assert.assertEquals(conference.getTotalSlots(), 80);
		Assert.assertEquals(conference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013,8,24,9,32,8));
		Assert.assertEquals(conference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2013,10,2,1,43,14));
		Assert.assertEquals(conference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013,4,10,20,58,35));
		Assert.assertEquals(conference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013,5,22,17,53,8));
	}
	
	@Test(groups="db-integration-tests")
	public void checkPageFetch()
	{
		ConferenceEntity conference = conferenceService.fetchConferenceBy(UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		
		Assert.assertNotNull(conference);
		
		Assert.assertNotNull(conference.getPages());
		Assert.assertFalse(conference.getPages().isEmpty());
		Assert.assertEquals(conference.getPages().size(), 3);

		Assert.assertEquals(conference.getPages().get(0).getTitle(), "About you");
		Assert.assertEquals(conference.getPages().get(1).getTitle(), "About your cat");
		Assert.assertEquals(conference.getPages().get(2).getTitle(), "Hobbies and activities");
	}
	
	@Test(groups="db-integration-tests")
	public void checkBlockFetch()
	{
		ConferenceEntity conference = conferenceService.fetchConferenceBy(UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		
		Assert.assertNotNull(conference);

		Assert.assertEquals(conference.getPages().get(0).getTitle(), "About you");
	
		Assert.assertNotNull(conference.getPages().get(0).getBlocks().get(0).getBlockType(), "paragraphContent");
	}
	
	@Test(groups="db-integration-tests")
	public void testUpdateConference() throws UnauthorizedException
	{
		ConferenceEntity conference = conferenceService.fetchConferenceBy(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa60"));
		
		Assert.assertEquals(conference.getName(), "Miami University Fall Retreat");
		
		conference.setName("Miami U Fall Retreat");
		
		conferenceService.updateConference(conference, testAppUser);
		
		ConferenceEntity updatedConference = conferenceService.fetchConferenceBy(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa60"));
		Assert.assertEquals(updatedConference.getName(), "Miami U Fall Retreat");
		
		updatedConference.setName("Miami University Fall Retreat");
		conferenceService.updateConference(updatedConference, testAppUser);
	}
	
	@Test(groups="db-integration-tests")
	public void testUpdateConferenceNotAuthorized() throws UnauthorizedException
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		ConferenceEntity conference = setupEm.find(ConferenceEntity.class,UUID.fromString("1cfa829f-2c3a-f803-a966-9a6510ee2f33"));
		
		/*this little misdirection makes it so that JPA doesn't change the entity on my behalf since it
		 * would still be managed.  transforming the object to the web API and back prevents that*/
		Conference conferenceToUpdate = Conference.fromJpa(conference);
		conferenceToUpdate.setName("Made up name");
		
		try
		{
			em.getTransaction().begin();
			conferenceService.updateConference(conferenceToUpdate.toJpaConferenceEntity(), testAppUserNotAuthorized);
			Assert.fail("Should have thrown UnauthorizedException");
		}
		catch(UnauthorizedException e)
		{
			/*do nothing*/
		}
		finally
		{
			em.getTransaction().rollback();
		}
		
	}
	
	/**
	 * Test: add a new page to an existing conference
	 * 
	 * Expected outcome: existing conference will have a new page added to it
	 * @throws UnauthorizedException 
	 */
	@Test(groups="db-integration-tests")
	public void addPageToConference() throws URISyntaxException, UnauthorizedException 
	{
		Page newPage = createFakePage();
		
		try
		{
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			setupEm.getTransaction().begin();
			conferenceService.addPageToConference(setupEm.find(ConferenceEntity.class, 
																	UUID.fromString("42e4c1B2-0cc1-89f7-9f4b-6bc3e0db5309")),
													newPage.toJpaPageEntity(),
													testAppUser);
			
			setupEm.flush();
			setupEm.getTransaction().commit();
			
			PageEntity persistedPage = setupEm.find(PageEntity.class, newPage.getId());
			
			/*make sure we get a copy of the page back*/
			Assert.assertNotNull(persistedPage);
			
			/*make sure the ID of the created page matches what was passed in*/
			Assert.assertEquals(persistedPage.getId(), newPage.getId());
			
			ConferenceEntity conference = setupEm.find(ConferenceEntity.class, UUID.fromString("42e4c1B2-0cc1-89f7-9f4b-6bc3e0db5309"));

			Assert.assertEquals(conference.getPages().size(), 4);
			Assert.assertEquals(conference.getPages().get(3).getId(), newPage.getId());
			
			setupEm.close();
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			removeAddedPage(cleanupEm, newPage.getId());
			cleanupEm.close();
		}
	}
	
	/**
	 * Test: add a new page to an existing conference
	 * 
	 * Expected outcome: existing conference will have not new page added to it
	 * @throws UnauthorizedException 
	 */
	@Test(groups="db-integration-tests")
	public void addPageToConferenceNotAuthorized() 
	{
		Page newPage = createFakePage();

		try
		{
			em.getTransaction().begin();
			conferenceService.addPageToConference(em.find(ConferenceEntity.class, 
					UUID.fromString("42e4c1B2-0cc1-89f7-9f4b-6bc3e0db5309")),
					newPage.toJpaPageEntity(),
					testAppUserNotAuthorized);
			
			
			Assert.fail("Should have thrown UnauthorizedException");
		}
		catch(UnauthorizedException e)
		{
			/*do nothing*/
		}

		finally
		{
			em.getTransaction().rollback();
		}
	}
	/**
	 * Test: create a new conference
	 * 
	 * Expected outcome: existing conference will have a new page added to it
	 * @throws UnauthorizedException 
	 */
	@Test(groups="db-integration-tests")
	public void createConference() throws UnauthorizedException
	{
		Conference conferenceToCreate = createFakeConference();
	
		try
		{
			em.getTransaction().begin();
			conferenceService.createNewConference(conferenceToCreate.toJpaConferenceEntity(), testAppUser);
			em.flush();
			em.getTransaction().commit();
			
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			ConferenceEntity persistedConference = setupEm.find(ConferenceEntity.class, conferenceToCreate.getId());
			
			Assert.assertNotNull(persistedConference);
			Assert.assertEquals(persistedConference.getId(), conferenceToCreate.getId());
			Assert.assertEquals(persistedConference.getName(), conferenceToCreate.getName());
			Assert.assertEquals(persistedConference.getTotalSlots(),  conferenceToCreate.getTotalSlots());
			Assert.assertEquals(persistedConference.getContactUser(), testAppUser.getId());
			Assert.assertEquals(persistedConference.getPages().size(), 0);
			//not checking times, at the point
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			removeAddedConference(cleanupEm, conferenceToCreate.getId());
			cleanupEm.close();
		}
	}
	
	/**
	 * Test: create a new conference
	 * 
	 * Expected outcome: no conference created, b/c no user id
	 * @throws UnauthorizedException 
	 */
	@Test(groups="db-integration-tests")
	public void createConferenceNotAuthorized() throws UnauthorizedException
	{
		Conference conferenceToCreate = createFakeConference();

		try
		{
			em.getTransaction().begin();
			conferenceService.createNewConference(conferenceToCreate.toJpaConferenceEntity(), null);
			Assert.fail("Should have thrown UnauthorizedException");
		}
		catch(UnauthorizedException e)
		{
			/*do nothing*/
		}
		finally
		{
			em.getTransaction().rollback();
		}
	}
	
	private Conference createFakeConference()
	{
		Conference fakeConference = new Conference();
		fakeConference.setId(UUID.randomUUID());
		fakeConference.setName("Fake Fall Retreat");
		fakeConference.setTotalSlots(202);
		fakeConference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 6, 1, 8, 0, 0));
		fakeConference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 6, 22, 23, 59, 59));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 4, 15, 0, 0));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 9, 11, 0, 0));
		fakeConference.setRegistrationPages(new ArrayList<Page>());
		
		return fakeConference;
	}
	
	private void removeAddedConference(EntityManager setupEm, UUID conferenceId)
	{
		ConferenceEntity conferenceToDelete = setupEm.find(ConferenceEntity.class, conferenceId);
		if(conferenceToDelete == null) return;
		
		setupEm.getTransaction().begin();
		setupEm.remove(conferenceToDelete);
		setupEm.flush();
		setupEm.getTransaction().commit();
	}
	
	private Page createFakePage()
	{
		Page fakePage = new Page();
		
		fakePage.setTitle("Ministry Prefs");
		fakePage.setId(UUID.randomUUID());
		fakePage.setPosition(1);
		fakePage.setBlocks(null);
		
		return fakePage;
	}
	
	private void removeAddedPage(EntityManager setupEm, UUID pageId)
	{
		PageEntity pageToDelete = setupEm.find(PageEntity.class, pageId);
		if(pageToDelete == null) return;
		
		setupEm.getTransaction().begin();
		setupEm.remove(pageToDelete);
		setupEm.flush();
		setupEm.getTransaction().commit();
	}
}