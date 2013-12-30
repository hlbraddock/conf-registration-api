 package org.cru.crs.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.UserInfo;
import org.joda.time.DateTime;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationServiceTest
{
	Connection sqlConnection;
	RegistrationService registrationService;
	
	@BeforeMethod
	private void setupConnectionAndService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		registrationService = new RegistrationService(sqlConnection, new AnswerService(sqlConnection), new PaymentService(sqlConnection));
	}

	@Test
	public void testGetRegistration()
	{
		RegistrationEntity registration = registrationService.getRegistrationBy(UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));
		
		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));
		Assert.assertEquals(registration.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(registration.getUserId(), UserInfo.Id.TestUser);
		Assert.assertFalse(registration.getCompleted());
		Assert.assertNull(registration.getCompletedTimestamp());
		Assert.assertEquals(registration.getTotalDue(), new BigDecimal("0"));
	}


	@Test
	public void testGetRegistrationByConferenceIdUserId()
	{
		RegistrationEntity registration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan, UserInfo.Id.TestUser);
		
		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));
		Assert.assertEquals(registration.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(registration.getUserId(), UserInfo.Id.TestUser);
		Assert.assertFalse(registration.getCompleted());
		Assert.assertNull(registration.getCompletedTimestamp());
		Assert.assertEquals(registration.getTotalDue(), new BigDecimal("0"));
	}
	
	@Test
	public void testCreateNewRegistration()
	{
		RegistrationEntity newRegistration = new RegistrationEntity();
		UUID id = UUID.randomUUID();
		DateTime completed = new DateTime();
		
		newRegistration.setId(id);
		newRegistration.setUserId(UserInfo.Id.Email);
		newRegistration.setConferenceId(ConferenceInfo.Id.NorthernMichigan);
		newRegistration.setCompleted(true);
		newRegistration.setCompletedTimestamp(completed);
		newRegistration.setTotalDue(new BigDecimal("48.12"));
		
		try
		{
			registrationService.createNewRegistration(newRegistration);
			
			RegistrationEntity retrievedRegistration = registrationService.getRegistrationBy(id);
			
			Assert.assertNotNull(retrievedRegistration);
			Assert.assertEquals(retrievedRegistration.getId(),id);
			Assert.assertEquals(retrievedRegistration.getUserId(),UserInfo.Id.Email);
			Assert.assertEquals(retrievedRegistration.getConferenceId(),ConferenceInfo.Id.NorthernMichigan);
			Assert.assertTrue(retrievedRegistration.getCompleted());
			Assert.assertEquals(retrievedRegistration.getCompletedTimestamp(),completed);
			Assert.assertEquals(retrievedRegistration.getTotalDue(),new BigDecimal("48.12"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testDeleteRegistration()
	{
		try
		{
			registrationService.deleteRegistration(UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));
			
			Assert.assertNull(registrationService.getRegistrationBy(UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7")));
			Assert.assertNull(registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan, UserInfo.Id.TestUser));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testIsUserRegistered()
	{
		Assert.assertTrue(registrationService.isUserRegistered(ConferenceInfo.Id.NorthernMichigan, UserInfo.Id.TestUser));
		Assert.assertFalse(registrationService.isUserRegistered(ConferenceInfo.Id.NorthernMichigan, UserInfo.Id.Ryan));
	}
	
}
