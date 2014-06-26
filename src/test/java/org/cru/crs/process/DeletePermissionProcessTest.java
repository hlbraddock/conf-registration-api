package org.cru.crs.process;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.process.DeletePermissionProcess;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.service.PermissionService;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

/**
 * Created by ryancarlson on 6/26/14.
 */
public class DeletePermissionProcessTest extends AbstractTestWithDatabaseConnectivity
{
	private DeletePermissionProcess deletePermissionProcess;
	private PermissionService permissionService;

	private UUID updatePermissionId = UUID.fromString("dcb85040-76e2-11e3-981f-0800200c9a66");
	private UUID creatorPermissionId = UUID.fromString("55dcfe17-4b09-4719-a201-d47b7d3568d4");

	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		refreshConnection();

		permissionService = ServiceFactory.createPermissionService(sqlConnection);

		deletePermissionProcess = new DeletePermissionProcess(permissionService);
	}


	@Test(groups="dbtest")
	public void testDeletePermissionOkay()
	{
		try
		{
			deletePermissionProcess.deletePermission(updatePermissionId);

			Assert.assertNull(permissionService.getPermissionBy(updatePermissionId));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest", expectedExceptions = BadRequestException.class)
	public void testDeleteLastCreatorPermission()
	{
		try
		{
			deletePermissionProcess.deletePermission(creatorPermissionId);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest")
	public void testDeleteNonLastCreatorPermission()
	{
		PermissionEntity newFullPermission = new PermissionEntity();

		newFullPermission.setId(UUID.randomUUID());
		newFullPermission.setConferenceId(ConferenceInfo.Id.MiamiUniversity);
		newFullPermission.setPermissionLevel(PermissionLevel.FULL);
		newFullPermission.setLastUpdatedTimestamp(new DateTime());
		newFullPermission.setGivenByUserId(UserInfo.Id.TestUser);
		newFullPermission.setUserId(UserInfo.Id.Ryan);


		try
		{
			permissionService.insertPermission(newFullPermission);

			deletePermissionProcess.deletePermission(creatorPermissionId);

			Assert.assertNull(permissionService.getPermissionBy(creatorPermissionId));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
