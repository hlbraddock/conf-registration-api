package org.cru.crs.service;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.cru.crs.api.model.Page;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
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

	private CrsApplicationUser testAppUser = new CrsApplicationUser(UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898"), AuthenticationProviderType.RELAY, "crs.testuser@crue.org");
	private CrsApplicationUser testAppUserNotAuthorized = new CrsApplicationUser(UUID.randomUUID(), null, null);
	
	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();

		pageService = new PageService(em, new ConferenceService(em, new UserService(em), new AnswerService(em)), new AnswerService(em));

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
		Assert.assertEquals(page.getTitle(), "About you");
		Assert.assertEquals(page.getPosition(), 0);
		Assert.assertEquals(page.getConferenceId(), UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
	}

	@Test(groups="db-integration-tests")
	public void testUpdatePage() throws UnauthorizedException
	{
		PageEntity page = pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(page.getTitle(), "Hobbies and activities");

		Page webPage = Page.fromJpa(page);
		webPage.setTitle("Fun stuff");

		try
		{
			em.getTransaction().begin();

			pageService.updatePage(webPage.toJpaPageEntity(), testAppUser);

			em.getTransaction().commit();
		}
		catch(Exception e)
		{
			em.getTransaction().rollback();
			Assert.fail("failed updating page", e);
		}
		
		PageEntity updatedPage = em.find(PageEntity.class, UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(updatedPage.getId(), UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));
		Assert.assertEquals(updatedPage.getTitle(), "Fun stuff");

		updatedPage.setTitle("Hobbies and activities");
		
		em.getTransaction().begin();
		em.merge(updatedPage);
		em.getTransaction().commit();
	}
	
	public void testUpdatePageNotAuthorized() throws UnauthorizedException
	{
		PageEntity page = pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(page.getTitle(), "Hobbies and activities");

		Page webPage = Page.fromJpa(page);
		webPage.setTitle("Fun stuff");

		try
		{
			em.getTransaction().begin();
			pageService.updatePage(webPage.toJpaPageEntity(), testAppUserNotAuthorized);
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
		page.setTitle("New Page");
		page.setPosition(3);
		page.setConferenceId(UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));

		try
		{
			setupEm.getTransaction().begin();

			ConferenceEntity conferenceToAddPageTo = setupEm.find(ConferenceEntity.class, UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));

			conferenceToAddPageTo.getPages().add(page);

			setupEm.flush();
			setupEm.getTransaction().commit();
		}
		catch(Exception e)
		{
			setupEm.getTransaction().rollback();
			setupEm.close();
			Assert.fail("failed setting up page to delete", e);
		}

		Assert.assertNotNull(em.find(PageEntity.class, page.getId()));

		try
		{
			em.getTransaction().begin();

			pageService.deletePage(page.getId(), testAppUser);

			em.flush();
			em.getTransaction().commit();
		}
		catch(Exception e)
		{
			em.getTransaction().rollback();
			Assert.fail("failed deleting the page", e);
		}
		
		EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		Assert.assertNull(cleanupEm.find(PageEntity.class, page.getId()));
	}

	@Test(groups="db-integration-tests")
	public void testDeletePageNotAuthorized() throws UnauthorizedException
	{
		try
		{
			em.getTransaction().begin();
			pageService.deletePage(UUID.fromString("bf37618e-4f86-2df5-8ae9-0ed3be0ed248"),testAppUserNotAuthorized);
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