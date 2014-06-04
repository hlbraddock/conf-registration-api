package org.cru.crs.service;

import java.util.UUID;

import org.cru.crs.AbstractServiceTest;
import org.cru.crs.model.UserEntity;
import org.cru.crs.utils.UserInfo;
import org.sql2o.Sql2oException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserServiceTest extends AbstractServiceTest
{
	UserService userService;

	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{
		refreshConnection();
		userService = new UserService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetUser()
	{
		UserEntity testUser = userService.getUserById(UserInfo.Id.TestUser);
		
		Assert.assertNotNull(testUser);
		Assert.assertEquals(testUser.getId(), UserInfo.Id.TestUser);
		Assert.assertEquals(testUser.getEmailAddress(), UserInfo.Email.TestUser);
		Assert.assertNull(testUser.getPhoneNumber());
		Assert.assertEquals(testUser.getFirstName(), "Test");
		Assert.assertEquals(testUser.getLastName(), "User");
	}
	
	@Test(groups="dbtest")
	public void testCreateUser()
	{
		UUID id = UUID.randomUUID();

		UserEntity newUserEntity = new UserEntity(id, "New T.", "User", "new.user@cru.org", "419/555-5555");
		
		try
		{
			userService.createUser(newUserEntity);
			
			UserEntity retrievedUser = userService.getUserById(id);
			
			Assert.assertEquals(retrievedUser.getId(), id);
			Assert.assertEquals(retrievedUser.getEmailAddress(), newUserEntity.getEmailAddress());
			Assert.assertEquals(retrievedUser.getFirstName(), newUserEntity.getFirstName());
			Assert.assertEquals(retrievedUser.getLastName(), newUserEntity.getLastName());
			Assert.assertEquals(retrievedUser.getPhoneNumber(), newUserEntity.getPhoneNumber());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest")
	public void testDeleteUser()
	{
		UUID id = UUID.randomUUID();

		UserEntity newUserEntity = new UserEntity(id, "New T.", "User", "new.user@cru.org", "419/555-5555");

		try
		{
			userService.createUser(newUserEntity);

			Assert.assertNotNull(userService.getUserById(id));

			userService.deleteUser(id);

			Assert.assertNull(userService.getUserById(id));
		}
		finally
		{
			sqlConnection.rollback();
		}

	}

	@Test(groups="dbtest", expectedExceptions = Sql2oException.class)
	public void testDeleteUserConstraintViolation()
	{
		UUID id = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f988343");

		try
		{
			userService.deleteUser(id);

			Assert.assertNull(userService.getUserById(id));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
