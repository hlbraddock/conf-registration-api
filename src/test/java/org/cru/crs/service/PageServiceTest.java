package org.cru.crs.service;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.cru.crs.api.model.Page;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups="db-integration-tests")
public class PageServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	private EntityManagerFactory emFactory;
	private EntityManager em;

	private PageService pageService;

	private UUID testAppUserId = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f9883c1");

	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();

		pageService = new PageService(em, new ConferenceService(em));
	}

	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}

	@Test(groups="db-integration-tests")
	public void fetchPageById()
	{
		PageEntity page = pageService.fetchPageBy(UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));

		Assert.assertNotNull(page);
		Assert.assertEquals(page.getName(), "About you");
		Assert.assertEquals(page.getPosition(), 0);
		Assert.assertEquals(page.getConferenceId(), UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
	}

	@Test(groups="db-integration-tests")
	public void testUpdatePage() throws UnauthorizedException
	{
		PageEntity page = pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(page.getName(), "Hobbies and activities");

		Page webPage = Page.fromJpa(page);
		webPage.setName("Fun stuff");

		pageService.updatePage(webPage.toJpaPageEntity(), testAppUserId);

		PageEntity updatedPage = em.find(PageEntity.class, UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(updatedPage.getId(), UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));
		Assert.assertEquals(updatedPage.getName(), "Fun stuff");

		updatedPage.setName("Hobbies and activities");
		em.merge(updatedPage);
	}
	
	public void testUpdatePageNotAuthorized() throws UnauthorizedException
	{
		PageEntity page = pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(page.getName(), "Hobbies and activities");

		Page webPage = Page.fromJpa(page);
		webPage.setName("Fun stuff");

		try
		{
			em.getTransaction().begin();
			pageService.updatePage(webPage.toJpaPageEntity(), UUID.randomUUID());
			Assert.fail("Should have thrown an UnauthorizedException");
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

	@Test(groups="db-integration-tests")
	public void testDeletePage() throws UnauthorizedException
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();

		PageEntity page = new PageEntity();

		page.setId(UUID.randomUUID());
		page.setName("New Page");
		page.setConferenceId(UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));

		setupEm.getTransaction().begin();

		ConferenceEntity conferenceToAddPageTo = setupEm.find(ConferenceEntity.class, UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));

		conferenceToAddPageTo.getPages().add(page);

		setupEm.flush();
		setupEm.getTransaction().commit();

		Assert.assertNotNull(em.find(PageEntity.class, page.getId()));

		em.getTransaction().begin();

		pageService.deletePage(page, testAppUserId);

		em.flush();
		em.getTransaction().commit();

		EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		Assert.assertNull(cleanupEm.find(PageEntity.class, page.getId()));
	}

	@Test(groups="db-integration-tests")
	public void testDeletePageNotAuthorized() throws UnauthorizedException
	{
		PageEntity page = new PageEntity();

		page.setId(UUID.randomUUID());
		page.setName("New Page");
		page.setConferenceId(UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));

		try
		{
			em.getTransaction().begin();
			pageService.deletePage(page, UUID.randomUUID());
			Assert.fail("Should have thrown an UnauthorizedException");
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
}