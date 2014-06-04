package org.cru.crs.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonProcessingException;
import org.cru.crs.AbstractServiceTest;
import org.cru.crs.model.RegistrationViewEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.JsonNodeHelper;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationViewServiceTest extends AbstractServiceTest
{
	RegistrationViewService registrationViewService;

	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{
		refreshConnection();
		registrationViewService = ServiceFactory.createRegistrationViewService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetDataViewById() throws JsonProcessingException, IOException
	{
		RegistrationViewEntity noCats = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
		
		Assert.assertNotNull(noCats);
		
		Assert.assertEquals(noCats.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(noCats.getCreatedByUserId(), UserInfo.Id.TestUser);
		Assert.assertEquals(noCats.getName(), "No cats");
		Assert.assertEquals(noCats.getVisibleBlockIds(), JsonNodeHelper.toJsonNode("[\"AF60D878-4741-4F21-9D25-231DB86E43EE\",\"DDA45720-DE87-C419-933A-018712B152D2\"]"));
	}
	
	@Test(groups="dbtest")
	public void testGetDataViewsForConference()
	{
		List<RegistrationViewEntity> northernMichiganDataViews =  registrationViewService.getRegistrationViewsForConference(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(northernMichiganDataViews);
		Assert.assertEquals(northernMichiganDataViews.size(), 2);
	}
	
	@Test(groups="dbtest")
	public void testInsertDataView() throws JsonProcessingException, IOException
	{
		UUID idForThisTest = UUID.randomUUID();
		
		try
		{
			registrationViewService.insertRegistrationView(createFakeDataView(idForThisTest));
			
			RegistrationViewEntity retrievedDataView = registrationViewService.getRegistrationViewById(idForThisTest);
			
			Assert.assertNotNull(retrievedDataView);
			Assert.assertEquals(retrievedDataView.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
			Assert.assertEquals(retrievedDataView.getCreatedByUserId(), UserInfo.Id.TestUser);
			Assert.assertEquals(retrievedDataView.getName(), "Foo");
			Assert.assertEquals(retrievedDataView.getVisibleBlockIds(), JsonNodeHelper.toJsonNode("[\"DDA45720-DE87-C419-933A-018712B152DC\"]"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest")
	public void testUpdateDataView()
	{
		try
		{
			RegistrationViewEntity noCats = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
			
			noCats.setName("No kitties");
			
			registrationViewService.updateRegistrationView(noCats);
			
			RegistrationViewEntity retrievedDataView = registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
			
			Assert.assertEquals(retrievedDataView.getName(), "No kitties");
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest")
	public void testDeleteDataView()
	{
		try
		{
			registrationViewService.deleteRegistrationView(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997"));
			
			Assert.assertNull(registrationViewService.getRegistrationViewById(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997")));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	private RegistrationViewEntity createFakeDataView(UUID id) throws JsonProcessingException, IOException
	{
		RegistrationViewEntity dataView = new RegistrationViewEntity();
		
		dataView.setId(id);
		dataView.setConferenceId(ConferenceInfo.Id.NorthernMichigan);
		dataView.setCreatedByUserId(UserInfo.Id.TestUser);
		dataView.setName("Foo");
		dataView.setVisibleBlockIds(JsonNodeHelper.toJsonNode("[\"DDA45720-DE87-C419-933A-018712B152DC\"]"));
		
		return dataView;
	}
	
}