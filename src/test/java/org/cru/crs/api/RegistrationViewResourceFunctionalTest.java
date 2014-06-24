package org.cru.crs.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.client.RegistrationViewResourceClient;
import org.cru.crs.api.model.RegistrationView;
import org.cru.crs.model.RegistrationViewEntity;
import org.cru.crs.service.RegistrationViewService;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.JsonNodeHelper;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

public class RegistrationViewResourceFunctionalTest  extends AbstractTestWithDatabaseConnectivity
{

	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;

	RegistrationViewResourceClient registrationViewClient;
	
	RegistrationViewService registrationViewService;

	@BeforeMethod(alwaysRun = true)
	private void createClient()
	{
		refreshConnection();

        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        registrationViewClient = ProxyFactory.create(RegistrationViewResourceClient.class, restApiBaseUrl);
        
        registrationViewService = ServiceFactory.createRegistrationViewService(sqlConnection);
	}
	
	@Test(groups="functional-tests")
	public void testGetRegistrationView() throws JsonProcessingException, IOException {
		ClientResponse<RegistrationView> response = registrationViewClient.getRegistrationView(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"), 
																								UserInfo.AuthCode.TestUser);

		Assert.assertEquals(response.getStatus(), 200);

		RegistrationView registrationView = response.getEntity();
		
		Assert.assertNotNull(registrationView);
		Assert.assertEquals(registrationView.getId(), UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
		Assert.assertEquals(registrationView.getName(), "No cats");
		Assert.assertEquals(registrationView.getCreatedByUserId(), UserInfo.Id.TestUser);
		Assert.assertEquals(registrationView.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(registrationView.getVisibleBlockIds(), JsonNodeHelper.toJsonNode("[\"AF60D878-4741-4F21-9D25-231DB86E43EE\",\"DDA45720-DE87-C419-933A-018712B152D2\"]"));
	}
	
	@Test(groups="functional-tests")
	public void testGetRegistrationViewNotFound() {
		ClientResponse<RegistrationView> response = registrationViewClient.getRegistrationView(UUID.randomUUID(), UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 404);
	}
	
	@Test(groups="functional-tests")
	public void testUpdateRegistrationView() {
		RegistrationViewEntity noCats = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
		
		noCats.setName("No kittehs");
		
		try {
			ClientResponse response = registrationViewClient.updateRegistrationView(noCats.getId(),
																						UserInfo.AuthCode.TestUser,
																						RegistrationView.fromDb(noCats));
			
			Assert.assertEquals(response.getStatus(), 204);
			
			RegistrationViewEntity retrievedEntity = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
			
			Assert.assertEquals(retrievedEntity.getId(), UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
			Assert.assertEquals(retrievedEntity.getName(), "No kittehs");
		}
		finally {
			noCats.setName("No cats");
			
			registrationViewService.updateRegistrationView(noCats);
			sqlConnection.commit();
		}
		
	}
	
	@Test(groups="functional-tests")
	public void testDeleteRegistrationView() {
		RegistrationViewEntity noCats = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
		
		try {
			ClientResponse response = registrationViewClient.deleteRegistrationView(noCats.getId(), UserInfo.AuthCode.TestUser);
			
			Assert.assertEquals(response.getStatus(), 204);
			
			RegistrationViewEntity retrievedEntity = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
			
			Assert.assertNull(retrievedEntity);			
		}
		finally {
			if(registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997")) == null) {
				registrationViewService.insertRegistrationView(noCats);
				sqlConnection.commit();
			}
		}
	}
}
