package org.cru.crs.api;

import java.util.UUID;

import org.cru.crs.api.client.PageResourceClient;
import org.cru.crs.api.model.Page;
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
		
		Assert.assertEquals(page.getTitle(),"About your cat");
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
}