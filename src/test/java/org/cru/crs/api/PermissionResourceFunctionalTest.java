package org.cru.crs.api;

import java.util.UUID;

import org.cru.crs.AbstractServiceTest;
import org.cru.crs.api.client.PermissionResourceClient;
import org.cru.crs.api.model.Permission;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PermissionResourceFunctionalTest  extends AbstractServiceTest
{

	static final String RESOURCE_PREFIX = "rest";

	PermissionResourceClient permissionClient;

	Environment environment = Environment.LOCAL;
	
	ConferenceService conferenceService;
	PermissionService permissionService;

	@BeforeMethod(alwaysRun = true)
	private void createClient()
	{
		refreshConnection();

        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        permissionClient = ProxyFactory.create(PermissionResourceClient.class, restApiBaseUrl);

        permissionService = ServiceFactory.createPermissionService(sqlConnection);
        conferenceService = ServiceFactory.createConferenceService(sqlConnection);
	}
	
	@Test(groups="functional-tests")
	public void testGetPermission() {
		
		ClientResponse<Permission> response = permissionClient.getPermission(UUID.fromString("1f790fa0-770b-11e3-981f-0800200c9a66"), UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);
		
		Permission permission = response.getEntity();
		
		Assert.assertNotNull(permission);
		Assert.assertEquals(permission.getUserId(), UserInfo.Id.TestUser);
		Assert.assertEquals(permission.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
		Assert.assertEquals(permission.getPermissionLevel(), PermissionLevel.CREATOR);
	}
	
	@Test(groups="functional-tests")
	public void testUpdatePermission()
	{
		PermissionEntity permission = permissionService.getPermissionBy(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));

		try
		{
			permission.setPermissionLevel(PermissionLevel.FULL);

			ClientResponse response = permissionClient.updatePermission(permission.getId(), UserInfo.AuthCode.TestUser, Permission.fromDb(permission));

			Assert.assertEquals(response.getStatus(), 204);

			PermissionEntity retrievedPermission = permissionService.getPermissionBy(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));

			Assert.assertNotNull(retrievedPermission);
			Assert.assertEquals(retrievedPermission.getPermissionLevel(), PermissionLevel.FULL);
			/*timestamp should have been updated by our call, so check that it's not the original timestamp*/
			Assert.assertNotEquals(retrievedPermission.getLastUpdatedTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 49));
		}
		finally
		{
			permission.setPermissionLevel(PermissionLevel.UPDATE);
			permission.setLastUpdatedTimestamp(DateTimeCreaterHelper.createDateTime(2013, 8, 14, 15, 27, 49));
			
			permissionService.updatePermission(permission);
			sqlConnection.commit();
		}
	}
	
	@Test(groups="functional-tests")
	public void testAcceptPermission()
	{
		PermissionEntity permissionBeforeAcceptance = permissionService.getPermissionBy(UUID.fromString("7cc69410-7eeb-11e3-baa7-0800200c9a66"));
		
		try
		{
			Assert.assertNull(permissionBeforeAcceptance.getUserId());
			
			ClientResponse response = permissionClient.acceptPermission("ABC123", UserInfo.AuthCode.Email);
			
			Assert.assertEquals(response.getStatus(), 204);
			
			PermissionEntity retrievedPermission = permissionService.getPermissionBy(UUID.fromString("7cc69410-7eeb-11e3-baa7-0800200c9a66"));
			
			Assert.assertEquals(retrievedPermission.getUserId(), UserInfo.Id.Email);
			Assert.assertEquals(retrievedPermission.getActivationCode(), "ABC123");
			
		}
		finally
		{
			permissionService.updatePermission(permissionBeforeAcceptance);
			sqlConnection.commit();
		}
	}
	
	@Test(groups="functional-tests")
	public void testDeletePermission()
	{
		PermissionEntity originalPermission = permissionService.getPermissionBy(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));
		
		try
		{
			ClientResponse response = permissionClient.revokePermission(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"), UserInfo.AuthCode.TestUser);
			
			Assert.assertEquals(response.getStatus(), 204);
			
			PermissionEntity revokedPermission = permissionService.getPermissionBy(UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66"));
			
			Assert.assertNull(revokedPermission);
		}
		finally
		{
			permissionService.insertPermission(originalPermission);
			sqlConnection.commit();
		}
	}
}
