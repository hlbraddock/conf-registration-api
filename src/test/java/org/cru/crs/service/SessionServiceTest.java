package org.cru.crs.service;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;


/**
 * These tests are meant to be run on a fresh database.
 * 
 * @author ryancarlson
 *
 */
public class SessionServiceTest extends AbstractTestWithDatabaseConnectivity
{
	SessionService sessionService;

	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{
		refreshConnection();
		sessionService = new SessionService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetSessionByAuthCodeForTestUser()
	{
		SessionEntity session = sessionService.getSessionByAuthCode(UserInfo.AuthCode.TestUser);
		
		Assert.assertNotNull(session);
		Assert.assertEquals(session.getAuthCode(), UserInfo.AuthCode.TestUser);
		Assert.assertEquals(session.getAuthProviderId(), UUID.fromString("36f19114-f833-4a26-b7ba-b67052b68cea"));
		Assert.assertEquals(session.getId(), UUID.fromString("d8a8c217-f977-4503-b3b3-85016f981234"));

		// this will break if the test user session expiration has been updated elsewhere (functional tests)
		// granted, these tests are to be run on a fresh database, but they all succeed regardless except for this one line
		// Assert.assertEquals(session.getExpiration(), DateTimeCreaterHelper.createDateTime(2014, 10, 2, 2, 43, 14));
	}
	
	@Test(groups="dbtest")
	public void testGetSessionByAuthCodeNotFound()
	{
		SessionEntity session = sessionService.getSessionByAuthCode("you won't find me");
		
		Assert.assertNull(session);
	}
	
	@Test(groups="dbtest")
	public void testFetchSessionsByUserAuthProviderId()
	{
		List<SessionEntity> sessions = sessionService.fetchSessionsByUserAuthProviderId(UUID.fromString("f8f8c217-f977-4503-b3b3-85016f988342"));
		
		Assert.assertNotNull(sessions);
		Assert.assertEquals(sessions.size(), 3);
	}
	
	@Test(groups="dbtest")
	public void testCreateSessionForEmailUser()
	{
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
