package org.cru.crs.service;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		pageService = new PageService(em);
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
	public void testUpdatePage()
	{
		PageEntity page = pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(page.getName(), "Hobbies and activities");

		page.setName("Fun stuff");

		pageService.updatePage(page);

		PageEntity updatedPage = em.find(PageEntity.class, UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));

		Assert.assertEquals(updatedPage.getId(), UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));
		Assert.assertEquals(updatedPage.getName(), "Fun stuff");

		updatedPage.setName("Hobbies and activities");
		em.merge(updatedPage);
	}

	@Test(groups="db-integration-tests")
	public void testDeletePage()
	{
		PageEntity page = new PageEntity();

		page.setId(UUID.randomUUID());
		page.setName("New Page");
		page.setPosition(3);
		page.setConferenceId(UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		
		em.persist(page);
		
		pageService.deletePage(page);
		
		Assert.assertNull(em.find(PageEntity.class, page.getId()));
	}
}