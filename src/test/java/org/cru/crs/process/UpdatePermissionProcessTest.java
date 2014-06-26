package org.cru.crs.process;

import junit.framework.Assert;
import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.model.Permission;
import org.cru.crs.api.process.UpdatePermissionProcess;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.service.PermissionService;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

public class UpdatePermissionProcessTest extends AbstractTestWithDatabaseConnectivity
{
	UpdatePermissionProcess updatePermissionProcess;
	
	PermissionService permissionService;
	
	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		refreshConnection();

		updatePermissionProcess = new UpdatePermissionProcess(ServiceFactory.createPermissionService(sqlConnection), new ClockImpl());
		
		permissionService = ServiceFactory.createPermissionService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testUpdatePermission()
	{
		try
		{
			updatePermissionProcess.acceptPermission(UserInfo.Users.Email, "ABC123");

			PermissionEntity retrievedPermissionAfterUpdate = permissionService.getPermissionBy(UUID.fromString("7cc69410-7eeb-11e3-baa7-0800200c9a66"));

			Assert.assertEquals(retrievedPermissionAfterUpdate.getUserId(), UserInfo.Id.Email);
			Assert.assertTrue(retrievedPermissionAfterUpdate.getLastUpdatedTimestamp().isBefore(new DateTime(DateTimeZone.UTC)));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest", expectedExceptions = BadRequestException.class)
	public void testUpdatePermissionNotAllowedNoRemainingAdmins()
	{
		try
		{
			PermissionEntity adminPermission = permissionService.getPermissionBy(UUID.fromString("55dcfe17-4b09-4719-a201-d47b7d3568d4"));
			adminPermission.setPermissionLevel(PermissionLevel.UPDATE);

			updatePermissionProcess.updatePermission(Permission.fromDb(adminPermission));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest")
	public void testAcceptPermission()
	{
		try
		{
			updatePermissionProcess.acceptPermission(UserInfo.Users.Email, "ABC123");

			PermissionEntity retrievedPermissionAfterUpdate = permissionService.getPermissionBy(UUID.fromString("7cc69410-7eeb-11e3-baa7-0800200c9a66"));

			Assert.assertEquals(retrievedPermissionAfterUpdate.getUserId(), UserInfo.Id.Email);
			Assert.assertTrue(retrievedPermissionAfterUpdate.getLastUpdatedTimestamp().isBefore(new DateTime(DateTimeZone.UTC)));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
