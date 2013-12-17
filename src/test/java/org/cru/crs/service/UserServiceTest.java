package org.cru.crs.service;

import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.UserEntity;
import org.cru.crs.utils.UserInfo;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserServiceTest
{

	Connection sqlConnection;
	UserService userService;
	
	@BeforeMethod
	private void setupConnectionAndService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		userService = new UserService(sqlConnection);
	}
	
	@Test
	public void testGetUser()
	{
		UserEntity testUser = userService.fetchUserBy(UserInfo.Id.TestUser);
		
		Assert.assertNotNull(testUser);
		Assert.assertEquals(testUser.getId(), UserInfo.Id.TestUser);
		Assert.assertNull(testUser.getEmailAddress());
		Assert.assertNull(testUser.getPhoneNumber());
		Assert.assertNull(testUser.getFirstName());
		Assert.assertNull(testUser.getLastName());
	}
	
	@Test
	public void testCreateUser()
	{
		UserEntity newUserEntity = new UserEntity();
		UUID id = UUID.randomUUID();
		
		newUserEntity.setId(id);
		newUserEntity.setEmailAddress("new.user@cru.org");
		newUserEntity.setFirstName("New T.");
		newUserEntity.setLastName("User");
		newUserEntity.setPhoneNumber("419/555-5555");
		
		try
		{
			userService.createUser(newUserEntity);
			
			UserEntity retrievedUser = userService.fetchUserBy(id);
			
			Assert.assertEquals(retrievedUser.getId(), id);
			Assert.assertEquals(retrievedUser.getEmailAddress(), "new.user@cru.org");
			Assert.assertEquals(retrievedUser.getFirstName(), "New T.");
			Assert.assertEquals(retrievedUser.getLastName(), "User");
			Assert.assertEquals(retrievedUser.getPhoneNumber(), "419/555-5555");
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
