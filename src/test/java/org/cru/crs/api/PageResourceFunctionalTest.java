package org.cru.crs.api;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.cru.crs.api.client.PageResourceClient;
import org.cru.crs.api.model.Page;
import org.cru.crs.model.PageEntity;
import org.cru.crs.utils.Environment;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups="functional-tests")
public class PageResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	
	Environment environment = Environment.LOCAL;
	PageResourceClient pageClient;
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        pageClient = ProxyFactory.create(PageResourceClient.class, restApiBaseUrl);
	}
	
	@Test(groups="functional-tests")
	public void getPage()
	{
		ClientResponse<Page> response = pageClient.getPage(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		
		Assert.assertEquals(response.getStatus(), 200);
		
		Page page = response.getEntity();
		
		Assert.assertEquals(page.getName(),"Ministry preferences");
		Assert.assertEquals(page.getPosition(), 1);
		Assert.assertEquals(page.getConferenceId(), UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		Assert.assertEquals(page.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
	}
	
	@Test(groups="functional-tests")
	public void getPageNotFound()
	{
		ClientResponse<Page> response = pageClient.getPage(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
		
		Assert.assertEquals(response.getStatus(), 404);
	}
	
	@Test(groups="functional-tests")
	public void updatePage()
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		
		try
		{
			PageEntity page = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));

			Assert.assertEquals(page.getName(), "Ministry preferences");

			//don't let JPA make the change for us...
			setupEm.detach(page);

			page.setName("Ministry Prefs");

			ClientResponse<Page> response = pageClient.updatePage(Page.fromJpa(page), page.getId());			
			
			//check the response, 204-No Content
			Assert.assertEquals(response.getStatus(), 204);
			
			PageEntity updatedPage = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			
			//this entity is still managed, so we should get the new value;
			Assert.assertEquals(updatedPage.getName(), "Ministry Prefs");
		}
		finally
		{
			PageEntity pageToRevert = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			
			//updatedPage is still managed, so setting the title back and flushing reverts the change
			pageToRevert.setName("Ministry preferences");
			setupEm.getTransaction().begin();
			setupEm.flush();
			setupEm.getTransaction().commit();
			setupEm.close();
		}
	}

	/**
	 * This test tests the fact that a PUT for a page with an ID that does not yet exist should just
	 * create the new page.  However, if it tries to associate it to a conference that doesn't exist,
	 * then we should get back 
	 */
	@Test(groups="functional-tests")
	public void updatePageWhichDoesNotExist()
	{
		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		
		try
		{
			PageEntity page = createFakePage();

			ClientResponse<Page> response = pageClient.updatePage(Page.fromJpa(page), page.getId());
			
			//check the response, 204-No Content
			Assert.assertEquals(response.getStatus(), 204);
			
			PageEntity newlyCreatedPage = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
			
			//check the new entity for proper values
			Assert.assertEquals(newlyCreatedPage.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
			Assert.assertEquals(newlyCreatedPage.getName(), "Ministry Prefs");
		}
		finally
		{
			PageEntity pageToDelete = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
			if(pageToDelete != null)
			{
				setupEm.getTransaction().begin();
				setupEm.remove(pageToDelete);
				setupEm.getTransaction().commit();
			}
		}
	}
	
	/**
	 * Trying to update a page on a conference that does not exist should return a 400
	 * Bad request error.
	 */
	public void updatePageWhichDoesNotExistOnConferenceThatDoesNotExist()
	{		
		PageEntity page = createFakePage();

		//now change the conference ID to one that does not exist
		page.setConferenceId(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa61"));

		ClientResponse<Page> response = pageClient.updatePage(Page.fromJpa(page), page.getId());

		//check the response, 400-Bad Request
		Assert.assertEquals(response.getStatus(), 400);
	}

	private PageEntity createFakePage()
	{
		PageEntity fakePage = new PageEntity();
		
		fakePage.setName("Ministry Prefs");
		fakePage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
		fakePage.setConferenceId(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa60"));
		fakePage.setPosition(7);
		fakePage.setBlocks(null);
		
		return fakePage;
	}
}