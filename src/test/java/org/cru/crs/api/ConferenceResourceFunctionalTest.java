package org.cru.crs.api;

import java.util.List;

import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.model.ConferenceEntity;
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
@Test
public class ConferenceResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	
	Environment environment = Environment.LOCAL;
	ConferenceResourceClient conferenceClient;
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
	}
	
	@Test
	public void fetchAllTheConferences()
	{
		ClientResponse<List<ConferenceEntity>> response = conferenceClient.getConferences();
		
		Assert.assertEquals(response.getStatus(), 200);
		List<ConferenceEntity> conferences = response.getEntity();
		
		Assert.assertNotNull(conferences);
	}
}
