package org.cru.crs.process;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.model.Permission;
import org.cru.crs.api.process.CreatePermissionProcess;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.service.PermissionService;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;
import org.cru.crs.utils.MailService;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

public class CreatePermissionProcessTest extends AbstractTestWithDatabaseConnectivity
{
	CreatePermissionProcess createPermissionProcess;
	
	PermissionService permissionService;
	
	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		refreshConnection();

		CrsProperties properties = new CrsPropertiesFactory().get();
		
		createPermissionProcess = new CreatePermissionProcess(ServiceFactory.createPermissionService(sqlConnection), 
																ServiceFactory.createConferenceService(sqlConnection), 
																ServiceFactory.createUserService(sqlConnection), 
																new MailService(properties),
																properties, 
																new ClockImpl());
		
		permissionService = ServiceFactory.createPermissionService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testCreatePermission() throws MalformedURLException, MessagingException
	{
		Permission newPermission = new Permission().withRandomID()
													.setConferenceId(ConferenceInfo.Id.WinterBeachCold)
													.setEmailAddress(UserInfo.Email.Ryan)
													.setPermissionLevel(PermissionLevel.UPDATE);

		try
		{
			createPermissionProcess.savePermission(newPermission, ConferenceInfo.Id.WinterBeachCold, UserInfo.Users.TestUser);

			PermissionEntity retrievedPermission = permissionService.getPermissionBy(newPermission.getId());

			Assert.assertEquals(retrievedPermission.getConferenceId(), ConferenceInfo.Id.WinterBeachCold);
			Assert.assertEquals(retrievedPermission.getEmailAddress(), UserInfo.Email.Ryan);
			Assert.assertEquals(retrievedPermission.getPermissionLevel(), PermissionLevel.UPDATE);
			Assert.assertEquals(retrievedPermission.getGivenByUserId(), UserInfo.Id.TestUser);
			Assert.assertNull(retrievedPermission.getUserId());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
