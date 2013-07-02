package org.cru.crs.api;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.model.ConferenceEntity;
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
@Test
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
	
	@Test
	public void fetchAllTheConferences()
	{
		ClientResponse<List<ConferenceEntity>> response = conferenceClient.getConferences();
		
		Assert.assertEquals(response.getStatus(), 200);
		List<ConferenceEntity> conferences = response.getEntity();
		
		Assert.assertNotNull(conferences);
	}
	
	@Test
	public void fetchConferenceById()
	{
		ClientResponse<ConferenceEntity> response = conferenceClient.getConference(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
		
		Assert.assertEquals(response.getStatus(), 200);
		ConferenceEntity conference = response.getEntity();
		
		Assert.assertNotNull(conference);
	}
	
	/**
	 * TODO: method needs to clean up after itself
	 * @throws URISyntaxException
	 */
	@Test
	public void createConference() throws URISyntaxException
	{
		ConferenceEntity fakeConference = createFakeConference();
		ClientResponse<ConferenceEntity> response = conferenceClient.createConference(fakeConference);
		String header = response.getHeaderAsLink("Location").getHref();
		
		Assert.assertEquals(response.getStatus(), 201);
		Assert.assertTrue(header.contains("http://localhost:8080/crs-http-json-api/rest/conferences/"));
		
		/**FIXME: this doesn't quite work yet b/c we don't have access to the confernce ID*/ 
		EntityManager cleanupEm = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
		cleanupEm.remove(fakeConference);
		
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
}
