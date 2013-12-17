package org.cru.crs.service;

import java.util.UUID;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.RelayUser;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.utils.UserInfo;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * These tests are meant to be run on a fresh database.
 * 
 * @author ryancarlson
 *
 */
public class AuthenticationProviderServiceTest
{
	Connection sqlConnection;
	AuthenticationProviderService authenticationProviderService;
	
	@BeforeMethod
	private void setupConnectionAndService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		authenticationProviderService = new AuthenticationProviderService(sqlConnection,new UserService(sqlConnection));
	}
	
	@Test
	public void testFindAuthProviderEntityById()
	{
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = authenticationProviderService.findAuthProviderIdentityById(UserInfo.AuthProviderId.TestUser);
		
		Assert.assertNotNull(authProviderIdentityEntity);
		Assert.assertEquals(authProviderIdentityEntity.getAuthProviderName(), UserInfo.Users.TestUser.getAuthProviderType().name());
		Assert.assertNull(authProviderIdentityEntity.getAuthProviderUserAccessToken());
		Assert.assertNull(authProviderIdentityEntity.getFirstName());
		Assert.assertNull(authProviderIdentityEntity.getLastName());
		Assert.assertEquals(authProviderIdentityEntity.getUsername(), UserInfo.Users.TestUser.getAuthProviderUsername());
		Assert.assertEquals(authProviderIdentityEntity.getUserAuthProviderId(), "05218422-6bbf-47fb-897c-371c91f87076"); /*ID within Relay, note data type is NOT UUID*/
		Assert.assertEquals(authProviderIdentityEntity.getId(), UserInfo.AuthProviderId.TestUser);  /*ID unique to this table*/
	}
	
	@Test
	public void testFindAuthProviderEntityUserByAuthProviderId()
	{
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = authenticationProviderService.findAuthProviderIdentityByUserAuthProviderId("05218422-6bbf-47fb-897c-371c91f87076");
		
		Assert.assertNotNull(authProviderIdentityEntity);
		Assert.assertEquals(authProviderIdentityEntity.getAuthProviderName(), UserInfo.Users.TestUser.getAuthProviderType().name());
		Assert.assertNull(authProviderIdentityEntity.getAuthProviderUserAccessToken());
		Assert.assertNull(authProviderIdentityEntity.getFirstName());
		Assert.assertNull(authProviderIdentityEntity.getLastName());
		Assert.assertEquals(authProviderIdentityEntity.getUsername(), UserInfo.Users.TestUser.getAuthProviderUsername());
		Assert.assertEquals(authProviderIdentityEntity.getUserAuthProviderId(), "05218422-6bbf-47fb-897c-371c91f87076"); /*ID within Relay, note data type is NOT UUID*/
		Assert.assertEquals(authProviderIdentityEntity.getId(), UserInfo.AuthProviderId.TestUser);  /*ID unique to this table*/
	}
	
	@Test
	public void testFindAuthProviderEntityByUsernameAndType()
	{
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderUsernameAndType("crs.testuser@crue.org", AuthenticationProviderType.RELAY);
		
		Assert.assertNotNull(authProviderIdentityEntity);
		Assert.assertEquals(authProviderIdentityEntity.getAuthProviderName(), UserInfo.Users.TestUser.getAuthProviderType().name());
		Assert.assertNull(authProviderIdentityEntity.getAuthProviderUserAccessToken());
		Assert.assertNull(authProviderIdentityEntity.getFirstName());
		Assert.assertNull(authProviderIdentityEntity.getLastName());
		Assert.assertEquals(authProviderIdentityEntity.getUsername(), UserInfo.Users.TestUser.getAuthProviderUsername());
		Assert.assertEquals(authProviderIdentityEntity.getUserAuthProviderId(), "05218422-6bbf-47fb-897c-371c91f87076"); /*ID within Relay, note data type is NOT UUID*/
		Assert.assertEquals(authProviderIdentityEntity.getId(), UserInfo.AuthProviderId.TestUser);  /*ID unique to this table*/
	}
	
	@Test
	public void testCreateUserAndAuthProviderEntities()
	{
		RelayUser newRelayUser = new RelayUser();

		String relayId = UUID.randomUUID().toString();
		
		newRelayUser.setId(relayId);
		newRelayUser.setFirstName("Unittest Fred");
		newRelayUser.setLastName("User");
		newRelayUser.setAuthenticationProviderType(AuthenticationProviderType.RELAY);
		newRelayUser.setUsername("unittest.fred@cru.org");
		
		try
		{
			authenticationProviderService.createIdentityAndAuthProviderRecords(newRelayUser);
			
			AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByUserAuthProviderId(relayId);
			UserEntity userEntity = new UserService(sqlConnection).fetchUserBy(authProviderEntity.getCrsId());
			
			Assert.assertNotNull(authProviderEntity);
			Assert.assertNotNull(authProviderEntity.getId()); /*this is set randomly by the service*/
			Assert.assertEquals(authProviderEntity.getAuthProviderName(), AuthenticationProviderType.RELAY.name());
			Assert.assertNull(authProviderEntity.getAuthProviderUserAccessToken()); /*not used by relay*/
			Assert.assertEquals(authProviderEntity.getFirstName(), "Unittest Fred");
			Assert.assertEquals(authProviderEntity.getLastName(), "User");
			Assert.assertEquals(authProviderEntity.getUserAuthProviderId(), relayId);
			Assert.assertEquals(authProviderEntity.getUsername(), "unittest.fred@cru.org");
			Assert.assertNotNull(authProviderEntity.getCrsId()); /*set randomly by the service*/
			
			Assert.assertNotNull(userEntity);
			Assert.assertEquals(userEntity.getId(), authProviderEntity.getCrsId());
			Assert.assertEquals(userEntity.getEmailAddress(), "unittest.fred@cru.org");
			Assert.assertEquals(userEntity.getFirstName(), "Unittest Fred");
			Assert.assertEquals(userEntity.getLastName(), "User");
		
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testUpdateAuthProviderType()
	{
		/*service method will be deprecated, so I'm not going to write the test*/
	}
}
