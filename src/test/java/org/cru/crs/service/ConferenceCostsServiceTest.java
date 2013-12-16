package org.cru.crs.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConferenceCostsServiceTest
{
	org.sql2o.Connection sqlConnection;
	
	@BeforeMethod
	private ConferenceCostsService getConferenceCostsService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		return new ConferenceCostsService(sqlConnection);
	}
	
	@Test
	public void testGetConferenceCosts()
	{
		ConferenceCostsEntity conferenceCostsEntity = getConferenceCostsService().fetchBy(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(conferenceCostsEntity);
		Assert.assertEquals(conferenceCostsEntity.getId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(conferenceCostsEntity.getBaseCost(), new BigDecimal("50.00"));
		Assert.assertEquals(conferenceCostsEntity.getMinimumDeposit(), new BigDecimal("10.00"));
		Assert.assertFalse(conferenceCostsEntity.isEarlyRegistrationDiscount());
		Assert.assertNull(conferenceCostsEntity.getEarlyRegistrationAmount());
		Assert.assertNull(conferenceCostsEntity.getEarlyRegistrationCutoff());
	}

	@Test
	public void testGetConferenceCostsWithEarlyRegistrationDiscount()
	{
		ConferenceCostsEntity conferenceCostsEntity = getConferenceCostsService().fetchBy(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
		
		Assert.assertNotNull(conferenceCostsEntity);
		Assert.assertEquals(conferenceCostsEntity.getId(), UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
		Assert.assertEquals(conferenceCostsEntity.getBaseCost(), new BigDecimal("75.00"));
		Assert.assertEquals(conferenceCostsEntity.getMinimumDeposit(), new BigDecimal("0.00"));
		Assert.assertTrue(conferenceCostsEntity.isEarlyRegistrationDiscount());
		Assert.assertEquals(conferenceCostsEntity.getEarlyRegistrationAmount(), new BigDecimal("15.00"));
		Assert.assertEquals(conferenceCostsEntity.getEarlyRegistrationCutoff(), DateTimeCreaterHelper.createDateTime(2013, 3, 31, 23, 59, 59));
	}
	
	@Test
	public void testSaveConferenceCosts()
	{
		ConferenceCostsService conferenceCostsService = getConferenceCostsService();
		UUID id = UUID.randomUUID();
		
		try
		{
			ConferenceCostsEntity newConferenceCostsEntity = new ConferenceCostsEntity();
			
			newConferenceCostsEntity.setId(id);
			newConferenceCostsEntity.setAcceptCreditCards(true);
			newConferenceCostsEntity.setAuthnetId("239458723");
			newConferenceCostsEntity.setAuthnetToken("2394871512345123");
			newConferenceCostsEntity.setBaseCost(new BigDecimal("45.00"));
			newConferenceCostsEntity.setEarlyRegistrationDiscount(false);
			newConferenceCostsEntity.setEarlyRegistrationAmount(new BigDecimal("0.00"));
			newConferenceCostsEntity.setEarlyRegistrationCutoff(null);
			newConferenceCostsEntity.setMinimumDeposit(new BigDecimal("0.00"));
			
			conferenceCostsService.saveNew(newConferenceCostsEntity);
			
			ConferenceCostsEntity retrievedConferenceCosts = conferenceCostsService.fetchBy(id);
			
			Assert.assertEquals(retrievedConferenceCosts.getId(),id);
			Assert.assertTrue(retrievedConferenceCosts.isAcceptCreditCards());
			Assert.assertEquals(retrievedConferenceCosts.getAuthnetId(), "239458723");
			Assert.assertEquals(retrievedConferenceCosts.getAuthnetToken(), "2394871512345123");
			Assert.assertEquals(retrievedConferenceCosts.getBaseCost(), new BigDecimal("45.00"));
			Assert.assertFalse(retrievedConferenceCosts.isEarlyRegistrationDiscount());
			Assert.assertEquals(retrievedConferenceCosts.getEarlyRegistrationAmount(), new BigDecimal("0.00"));
			Assert.assertEquals(retrievedConferenceCosts.getEarlyRegistrationCutoff(), null);
			Assert.assertEquals(retrievedConferenceCosts.getMinimumDeposit(), new BigDecimal("0.00"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testUpdateConfernenceCosts()
	{
		ConferenceCostsService conferenceCostsService = getConferenceCostsService();
		
		DateTime currentDateTime = new DateTime();
		
		ConferenceCostsEntity conferenceCostsEntityToUpdate = new ConferenceCostsEntity();
		
		conferenceCostsEntityToUpdate.setId(ConferenceInfo.Id.NorthernMichigan);
		conferenceCostsEntityToUpdate.setEarlyRegistrationDiscount(true);
		conferenceCostsEntityToUpdate.setEarlyRegistrationAmount(new BigDecimal("20.00"));
		conferenceCostsEntityToUpdate.setEarlyRegistrationCutoff(currentDateTime);
		conferenceCostsEntityToUpdate.setAuthnetId("123456789");
		conferenceCostsEntityToUpdate.setAuthnetToken("132490784098023");
		conferenceCostsEntityToUpdate.setAcceptCreditCards(true);
		conferenceCostsEntityToUpdate.setBaseCost(new BigDecimal("75.00"));
		conferenceCostsEntityToUpdate.setMinimumDeposit(new BigDecimal("0.00"));
		
		try
		{
			conferenceCostsService.update(conferenceCostsEntityToUpdate);
			
			ConferenceCostsEntity retrievedConferenceCosts = conferenceCostsService.fetchBy(ConferenceInfo.Id.NorthernMichigan);
			
			Assert.assertNotNull(retrievedConferenceCosts);
			
			Assert.assertEquals(conferenceCostsEntityToUpdate.getId(), ConferenceInfo.Id.NorthernMichigan);
			Assert.assertTrue(conferenceCostsEntityToUpdate.isEarlyRegistrationDiscount());
			Assert.assertEquals(conferenceCostsEntityToUpdate.getEarlyRegistrationAmount(), new BigDecimal("20.00"));
			Assert.assertEquals(conferenceCostsEntityToUpdate.getEarlyRegistrationCutoff(), currentDateTime);
			Assert.assertEquals(conferenceCostsEntityToUpdate.getAuthnetId(), "123456789");
			Assert.assertEquals(conferenceCostsEntityToUpdate.getAuthnetToken(), "132490784098023");
			Assert.assertTrue(conferenceCostsEntityToUpdate.isAcceptCreditCards());
			Assert.assertEquals(conferenceCostsEntityToUpdate.getBaseCost(), new BigDecimal("75.00"));
			Assert.assertEquals(conferenceCostsEntityToUpdate.getMinimumDeposit(), new BigDecimal("0.00"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
