package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * These tests are meant to be run on a fresh database.
 * 
 * @author ryancarlson
 *
 */
public class SessionServiceTest
{
	
	org.sql2o.Connection sqlConnection;
	
	@BeforeMethod
	private SessionService getSessionService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		return new SessionService(sqlConnection);
	}
	
	@Test
	public void testGetSessionByAuthCodeForTestUser()
	{
		SessionEntity session = getSessionService().getSessionByAuthCode(UserInfo.AuthCode.TestUser);
		
		Assert.assertNotNull(session);
		Assert.assertEquals(session.getAuthCode(), UserInfo.AuthCode.TestUser);
		Assert.assertEquals(session.getAuthProviderId(), UUID.fromString("36f19114-f833-4a26-b7ba-b67052b68cea"));
		Assert.assertEquals(session.getId(), UUID.fromString("d8a8c217-f977-4503-b3b3-85016f981234"));
		Assert.assertEquals(session.getExpiration(), DateTimeCreaterHelper.createDateTime(2014, 10, 2, 2, 43, 14));
	}
	
	@Test
	public void testGetSessionByAuthCodeNotFound()
	{
		SessionEntity session = getSessionService().getSessionByAuthCode("you won't find me");
		
		Assert.assertNull(session);
	}
	
	@Test
	public void testFetchSessionsByUserAuthProviderId()
	{
		List<SessionEntity> sessions = getSessionService().fetchSessionsByUserAuthProviderId(UUID.fromString("f8f8c217-f977-4503-b3b3-85016f988342"));
		
		Assert.assertNotNull(sessions);
		Assert.assertEquals(sessions.size(), 3);
	}
	
	@Test
	public void testCreateSessionForEmailUser()
	{
		SessionService sessionService = getSessionService();
		
		SessionEntity newSession = new SessionEntity();
		String authCode = AuthCodeGenerator.generate();
		UUID id = UUID.randomUUID();
		
		newSession.setId(id);
		newSession.setAuthProviderId(UserInfo.AuthProviderId.Email);
		newSession.setExpiration(DateTimeCreaterHelper.createDateTime(2015, 10, 2, 5, 2, 1));
		newSession.setAuthCode(authCode);

		try
		{
			sessionService.create(newSession);

			SessionEntity retrievedSession = sessionService.getSessionByAuthCode(authCode);
			
			Assert.assertNotNull(retrievedSession);
			Assert.assertEquals(retrievedSession.getAuthCode(), authCode);
			Assert.assertEquals(retrievedSession.getAuthProviderId(), UserInfo.AuthProviderId.Email);
			Assert.assertEquals(retrievedSession.getExpiration(), DateTimeCreaterHelper.createDateTime(2015, 10, 2, 5, 2, 1));
			Assert.assertEquals(retrievedSession.getId(), id);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
