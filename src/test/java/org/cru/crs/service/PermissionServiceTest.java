package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import org.ccci.util.NotImplementedException;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PermissionServiceTest
{
	org.sql2o.Connection sqlConnection;
	PermissionService permissionService;
	
	final UUID permissionIdToRyan = UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66");
	final UUID permissionIdToEmailUser = UUID.fromString("2230e3d0-76e3-11e3-981f-0800200c9a66");
	
	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		permissionService = new PermissionService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetPermissionById()
	{
		PermissionEntity permission = permissionService.getPermissionBy(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));
		
		Assert.assertNotNull(permission);
		Assert.assertEquals(permission.getId(), UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));
		Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
		Assert.assertEquals(permission.getUserId(), UserInfo.Id.Ryan);
		Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.UPDATE);
		Assert.assertEquals(permission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 49));
	}
	
	@Test(groups="dbtest")
	public void testGetPermissionByAuthCode()
	{
		PermissionEntity permission = permissionService.getPermissionByActivationCode("ABC123");
		
		Assert.assertNotNull(permission);
		Assert.assertEquals(permission.getId(), UUID.fromString("7cc69410-7eeb-11e3-baa7-0800200c9a66"));
		Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
		Assert.assertNull(permission.getUserId());
		Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.WinterBeachCold);
		Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.VIEW);
		Assert.assertEquals(permission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2014, 8, 14, 15, 27, 50));
	}
	
	@Test(groups="dbtest")
	public void testGetPermissionsForConference()
	{
		List<PermissionEntity> permissionsForNorthernMichiganConference = permissionService.getPermissionsForConference(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(permissionsForNorthernMichiganConference);
		Assert.assertEquals(permissionsForNorthernMichiganConference.size(), 3);
		
		for(PermissionEntity permission : permissionsForNorthernMichiganConference)
		{
			if(permission.getId().equals(permissionIdToRyan))
			{
				Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
				Assert.assertEquals(permission.getUserId(), UserInfo.Id.Ryan);
				Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
				Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.UPDATE);
				Assert.assertEquals(permission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 49));
			}
			else if(permission.getId().equals(permissionIdToEmailUser))
			{
				Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
				Assert.assertEquals(permission.getUserId(), UserInfo.Id.Email);
				Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
				Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.VIEW);
				Assert.assertEquals(permission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 50));
			}
			else if(permission.getId().equals(UUID.fromString("2230e3d0-76e3-11e3-981f-0800200c9a66")))
			{
				Assert.assertEquals(permission.getUserId(), UserInfo.Id.TestUser);
				Assert.assertNull(permission.getUserId());
				Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
				Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.CREATOR);
				Assert.assertNull(permission.getLastUpdatedTimestamp());
			}
		}
	}
	/**
	 * FIXME: this is a dumb test (written by me) and is currently failing
	 */
	@Test(groups="dbtest")
	public void testGetPermissionsForUser()
	{
		List<PermissionEntity> permissionsForRyan = permissionService.getPermissionsForUser(UserInfo.Id.Ryan);
		
		Assert.assertNotNull(permissionsForRyan);
		Assert.assertEquals(4, permissionsForRyan.size());

		for(PermissionEntity permission : permissionsForRyan) {
			Assert.assertNotNull(permission);
			
			if(permission.getId().equals(permissionIdToRyan)) {
				Assert.assertEquals(permission.getGivenByUserId(), UserInfo.Id.TestUser);
				Assert.assertEquals(permission.getUserId(), UserInfo.Id.Ryan);
				Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
				Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.UPDATE);
				Assert.assertEquals(permission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 49));
			}
		}
	}
	
	@Test(groups="dbtest")
	public void testGetByUserIdConferenceId()
	{
		PermissionEntity permission = permissionService.getPermissionForUserOnConference(UserInfo.Id.TestUser, ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(permission);
		Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.CREATOR);
	}
	
	@Test(groups="dbtest")
	public void testInsertNewPermission()
	{
		try
		{
			PermissionEntity newPermission = createFakePermission();
			UUID newPermissionId = newPermission.getId();
			
			permissionService.insertPermission(newPermission);
			
			PermissionEntity retrievedPermission = permissionService.getPermissionBy(newPermissionId);
			
			Assert.assertNotNull(retrievedPermission);
			Assert.assertEquals(retrievedPermission.getId(), newPermissionId);
			Assert.assertEquals(retrievedPermission.getConferenceId(), ConferenceInfo.Id.MiamiUniversity);
			Assert.assertEquals(retrievedPermission.getGivenByUserId(), UserInfo.Id.TestUser);
			Assert.assertEquals(retrievedPermission.getUserId(), UserInfo.Id.Ryan);
			Assert.assertEquals(retrievedPermission.getPermissionLevel(), PermissionLevel.FULL);
			Assert.assertEquals(retrievedPermission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2014, 2, 14, 21, 54, 54));
			
			
			Assert.assertEquals(permissionService.getPermissionsForUser(UserInfo.Id.Ryan).size(), 5);
			Assert.assertEquals(permissionService.getPermissionsForConference(ConferenceInfo.Id.MiamiUniversity).size(), 2);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest")
	public void testUpdatePermission()
	{
		try
		{
			PermissionEntity permissionToRyan = permissionService.getPermissionBy(permissionIdToRyan);

			Assert.assertNotEquals(permissionToRyan.getPermissionLevel(), PermissionLevel.FULL);
			
			permissionToRyan.setPermissionLevel(PermissionLevel.FULL);
			permissionToRyan.setLastUpdatedTimestamp(DateTimeCreaterHelper.createDateTime(2014, 1, 1, 10, 0, 0));

			permissionService.updatePermission(permissionToRyan);

			PermissionEntity retrievedPermission = permissionService.getPermissionBy(permissionIdToRyan);

			Assert.assertNotNull(retrievedPermission);
			Assert.assertEquals(retrievedPermission.getId(), UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));
			Assert.assertEquals(retrievedPermission.getGivenByUserId(), UserInfo.Id.TestUser);
			Assert.assertEquals(retrievedPermission.getUserId(), UserInfo.Id.Ryan);
			Assert.assertEquals(retrievedPermission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
			Assert.assertEquals(retrievedPermission.getPermissionLevel(), PermissionLevel.FULL);
			Assert.assertEquals(retrievedPermission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2014, 1, 1, 10, 0, 0));		
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest", expectedExceptions=NotImplementedException.class)
	public void testDeletePermission()
	{
		try
		{
			permissionService.deletePermission(permissionIdToRyan);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	private PermissionEntity createFakePermission()
	{
		PermissionEntity fakePermission = new PermissionEntity();
		
		fakePermission.setId(UUID.randomUUID());
		fakePermission.setConferenceId(ConferenceInfo.Id.MiamiUniversity);
		fakePermission.setGivenByUserId(UserInfo.Id.TestUser);
		fakePermission.setUserId(UserInfo.Id.Ryan);
		fakePermission.setPermissionLevel(PermissionLevel.FULL);
		fakePermission.setLastUpdatedTimestamp(DateTimeCreaterHelper.createDateTime(2014, 2, 14, 21, 54, 54));
		
		return fakePermission;
	}
}
