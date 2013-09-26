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

/**
 * This test requires an EE app server to be running to test the endpoint/resource
 * @author ryancarlson
 */
@Test(groups="functional-tests")
public class PageResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;
	PageResourceClient pageClient;

	private String authCodeRyan = "fd33c83b97b59dc3884454b7c2b861db03d5399c";
	private String authCodeEmail = "488aca23cecd6e5b8ac406bf74a46723dd853273";
	private String authCodeTestUser = "11eac4a91ccb730509cd82d822b5b4dd202de7ff";

	private UUID userUUIDRyan = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f9883c1");
	private UUID userUUIDEmail = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f988343");
	private UUID userUUIDTest = UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898");

	@BeforeMethod
	private void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        pageClient = ProxyFactory.create(PageResourceClient.class, restApiBaseUrl);
	}
	
	/**
	 * Test: find a page specified by ID
	 * 
	 * Expected outcome: page is found.
	 * 
	 * Input: UUID - 0a00d62c-af29-3723-f949-95a950a0b27c
	 * 
	 * Expected return: 200 - OK and Page JSON resource specified by input ID.
	 */
	@Test(groups="functional-tests")
	public void getPage()
	{
		ClientResponse<Page> response = pageClient.getPage(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		
		Assert.assertEquals(response.getStatus(), 200);
		
		Page page = response.getEntity();
		
		Assert.assertEquals(page.getTitle(),"Ministry preferences");
		Assert.assertEquals(page.getPosition(), 1);
		Assert.assertEquals(page.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
	}
	
	/**
	 * Test: test endpoint with id that does not exist
	 * 
	 * Expected outcome: page is not found.
	 * 
	 * Input: UUID - 0a00d62c-af29-3723-f949-95a950a0dddd
	 * 
	 * Expected return: 404 - NOT FOUND
	 */
	@Test(groups="functional-tests")
	public void getPageNotFound()
	{
		ClientResponse<Page> response = pageClient.getPage(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
		
		Assert.assertEquals(response.getStatus(), 404);
	}
	
	/**
	 * Test: test update endpoint with a valid page ID (path and body IDs match), by changing the name of the page
	 * 
	 * Expected outcome: page receives updated name
	 * 
	 * Input: UUID - 0a00d62c-af29-3723-f949-95a950a0b27c and JSON page where name = 'Ministry Prefs'
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups="functional-tests")
	public void updatePage()
	{	
		try
		{
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			PageEntity page = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));

			Assert.assertEquals(page.getTitle(), "Ministry preferences");

			Page webModelPage = Page.fromJpa(page);

			webModelPage.setTitle("Ministry Prefs");

			@SuppressWarnings("rawtypes")
			ClientResponse response = pageClient.updatePage(webModelPage, webModelPage.getId(), authCodeTestUser);
			
			//check the response, 204-No Content
			Assert.assertEquals(response.getStatus(), 204);
			
			//close the Entity manager, JPA can't tell the entity changed in the underlying DB
			setupEm.close();
			
			//create a new Entity Manager to verify the underlying change took place
			EntityManager postCheckEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			PageEntity updatedPage = postCheckEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			
			//this entity is still managed, so we should get the new value;
			Assert.assertEquals(updatedPage.getTitle(), "Ministry Prefs");
			
			postCheckEm.close();
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			PageEntity pageToRevert = cleanupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			
			//updatedPage is still managed, so setting the title back and flushing reverts the change
			pageToRevert.setTitle("Ministry preferences");
			cleanupEm.getTransaction().begin();
			cleanupEm.flush();
			cleanupEm.getTransaction().commit();
			cleanupEm.close();
		}
	}

	/**
	 * Test: test update endpoint with a valid page ID (path and body IDs match), but the page does not exist
	 * in the system.
	 * 
	 * Expected outcome: new page is created with the values specified in the JSON page resource
	 * 
	 * Input: UUID - 0a00d62c-af29-3723-f949-95a950a0dddd and JSON page resource
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups="functional-tests")
	public void updatePageWhichDoesNotExist()
	{
		try
		{
			EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			PageEntity page = createFakePage();

			@SuppressWarnings("rawtypes")
			ClientResponse response = pageClient.updatePage(Page.fromJpa(page), page.getId(), authCodeTestUser);
			
			//check the response, 204-No Content
			Assert.assertEquals(response.getStatus(), 204);
			
			PageEntity newlyCreatedPage = setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
			
			//check the new entity for proper values
			Assert.assertEquals(newlyCreatedPage.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
			Assert.assertEquals(newlyCreatedPage.getTitle(), "Ministry Prefs");
		}
		finally
		{
			EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
			
			PageEntity pageToDelete = cleanupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
			if(pageToDelete != null)
			{
				cleanupEm.getTransaction().begin();
				cleanupEm.remove(pageToDelete);
				cleanupEm.getTransaction().commit();
				cleanupEm.close();
			}
		}
	}
	
	/**
	 * Test: test update endpoint with a valid page ID (path and body IDs match), but the conference ID
	 * within the page resource is invalid
	 * 
	 * Expected outcome: update should fail since there is no conference to which this page can be associated
	 * 
	 * Input: JSON page resource with invalid conference ID
	 * 
	 * Expected output: 400 - BAD REQUEST
	 */
	@Test(groups="functional-tests")
	public void updatePageWhichDoesNotExistOnConferenceThatDoesNotExist()
	{		
		PageEntity page = createFakePage();

		//now change the conference ID to one that does not exist
		page.setConferenceId(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa61"));

		@SuppressWarnings("rawtypes")
		ClientResponse response = pageClient.updatePage(Page.fromJpa(page), page.getId(), authCodeTestUser);

		//check the response, 400-Bad Request
		Assert.assertEquals(response.getStatus(), 400);
	}
	
	/**
	 * Test: test update endpoint where the page ID specified in the path does not match the page ID
	 * in the body of the Page JSON resource
	 * 
	 * Expected outcome: update should fail since there is a mismatch in IDs
	 * 
	 * Input: JSON page resource with page ID that doesn't match path page ID
	 * 
	 * Expected output: 400 - BAD REQUEST
	 */
	@Test(groups="functional-tests")
	public void updatePageWherePathAndBodyPageIdsDontMatch()
	{		
		PageEntity page = createFakePage();

		@SuppressWarnings("rawtypes")
		ClientResponse response = pageClient.updatePage(Page.fromJpa(page), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0eeee"), authCodeTestUser);

		//check the response, 400-Bad Request
		Assert.assertEquals(response.getStatus(), 400);
	}

	/**
	 * Test: test delete page endpoint
	 * 
	 * Expected outcome: page resource specified by ID:  .. should be deleted
	 * 
	 * Input: JSON page resource with page ID: 
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups="functional-tests")
	public void deletePage()
	{	
//		EntityManager setupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
//		
//		PageEntity jpaPage = createFakePage();
//
//		setupEm.getTransaction().begin();
//
//		ConferenceEntity conference = setupEm.find(ConferenceEntity.class, UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa60"));
//		conference.getPages().add(jpaPage);
//		setupEm.getTransaction().commit();
//		
//		try
//		{
//			ClientResponse<Page> response = pageClient.deletePage(Page.fromJpa(jpaPage), jpaPage.getId());
//
//			Assert.assertEquals(response.getStatus(), 204);
//			Assert.assertNull(setupEm.find(PageEntity.class, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd")));
//		}
//		finally
//		{
//			setupEm.remove(jpaPage);
//		}
	}

	/**
	 * Test: test delete page endpoint with ID in path that doesn't match body ID
	 * 
	 * Expected outcome: endpoint should return 400 bad request
	 * 
	 * Input: JSON page resource with page ID - "0a00d62c-af29-3723-f949-95a950a0dddd" and path page ID - "0a00d62c-af29-3723-f949-95a950a0cccc"
	 * 
	 * Expected output: 400 - BAD REQEUST
	 */
	@Test(groups="functional-tests")
	public void deletePageWherePathAndBodyPageIdsDontMatch()
	{
		PageEntity jpaPage = createFakePage();
		
		@SuppressWarnings("rawtypes")
		ClientResponse response = pageClient.deletePage(Page.fromJpa(jpaPage), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"), authCodeTestUser);
		
		Assert.assertEquals(response.getStatus(), 400);
	}

	
	private PageEntity createFakePage()
	{
		PageEntity fakePage = new PageEntity();
		
		fakePage.setTitle("Ministry Prefs");
		fakePage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
		fakePage.setConferenceId(UUID.fromString("1951613e-a253-1af8-6bc4-c9f1d0b3fa60"));
		fakePage.setPosition(7);
		fakePage.setBlocks(null);
		
		return fakePage;
	}
}