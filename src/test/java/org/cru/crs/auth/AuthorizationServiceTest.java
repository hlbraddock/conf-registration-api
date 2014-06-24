package org.cru.crs.auth;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.jaxrs.UnauthorizedException;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class AuthorizationServiceTest extends AbstractTestWithDatabaseConnectivity
{
	AuthorizationService authorizationService;
	ConferenceService conferenceService;
	RegistrationService registrationService;
	
	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{
		refreshConnection();

		registrationService = ServiceFactory.createRegistrationService(sqlConnection);
		authorizationService = new AuthorizationService(new PermissionService(sqlConnection), registrationService);
		conferenceService = ServiceFactory.createConferenceService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetConference()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		/*any user should be able to read conference data*/
		authorizationService.authorizeConference(northernMichiganConference, OperationType.READ, UserInfo.Users.TestUser);
		authorizationService.authorizeConference(northernMichiganConference, OperationType.READ, UserInfo.Users.Ryan);
		authorizationService.authorizeConference(northernMichiganConference, OperationType.READ, UserInfo.Users.Email);
	}
	
	@Test(groups="dbtest")
	public void testUpdateConferenceAllowed()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		/*these users have CREATOR and UPDATE permissions*/
		authorizationService.authorizeConference(northernMichiganConference, OperationType.UPDATE, UserInfo.Users.TestUser);
		authorizationService.authorizeConference(northernMichiganConference, OperationType.UPDATE, UserInfo.Users.Ryan);
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testUpdateConferenceUnauthorized()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		/*this user only has VIEW permissions*/
		authorizationService.authorizeConference(northernMichiganConference, OperationType.UPDATE, UserInfo.Users.Email);
	}
	
	@Test(groups="dbtest")
	public void testAdminConferenceAllowed()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		/*this user has CREATOR permissions*/
		authorizationService.authorizeConference(northernMichiganConference, OperationType.ADMIN, UserInfo.Users.TestUser);
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testAdminConferenceUnauthorizedOne()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		/*this user only has UPDATE permissions*/
		authorizationService.authorizeConference(northernMichiganConference, OperationType.ADMIN, UserInfo.Users.Ryan);
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testAdminConferenceUnauthorizedTwo()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		
		/*this user only has VIEW permissions*/
		authorizationService.authorizeConference(northernMichiganConference, OperationType.ADMIN, UserInfo.Users.Email);
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testAdminConferenceUnauthorizedThree()
	{
		ConferenceEntity winterBeachConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.WinterBeachCold);
		/*this user has CREATOR permissions on the conference, so he can administer any registration*/
		authorizationService.authorizeConference(winterBeachConference, OperationType.ADMIN, UserInfo.Users.Ryan);
	}

	
	@Test(groups="dbtest")
	public void testAdminRegistrationAllowed()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		RegistrationEntity testUserRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan,
																											UserInfo.Id.TestUser);
		
		RegistrationEntity emailRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan, 
																											UserInfo.Id.Email);
		
		/*this user has CREATOR permissions on the conference, so he can administer any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.ADMIN, UserInfo.Users.TestUser);
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.ADMIN, UserInfo.Users.TestUser);
	}
		
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testAdminRegistrationUnauthorized()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		RegistrationEntity testUserRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan,
																											UserInfo.Id.TestUser);
		
		/*this user has UPDATE permissions on the conference, so he cannot administer any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.ADMIN, UserInfo.Users.Ryan);
	}
	
	@Test(groups="dbtest")
	public void testUpdateRegistrationAllowed()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		RegistrationEntity testUserRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan,
																											UserInfo.Id.TestUser);
		
		RegistrationEntity emailRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan, 
																											UserInfo.Id.Email);
		
		/*this user has CREATOR permissions on the conference, so he can update any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.UPDATE, UserInfo.Users.TestUser);
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.UPDATE, UserInfo.Users.TestUser);
		
		/*this user has UPDATE permissions on the conference, so he can update any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.UPDATE, UserInfo.Users.Ryan);
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.UPDATE, UserInfo.Users.Ryan);
		
		/*this user does not haveUPDATE permissions on the conference, but should be allowed to update his own registration*/
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.UPDATE, UserInfo.Users.Email);
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testUpdateRegistrationUnauthorized()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		RegistrationEntity testUserRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan,
																											UserInfo.Id.TestUser);
		
		/*this user has only VIEW permissions on the conference, so he cannot update any registration, other than his own*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.UPDATE, UserInfo.Users.Email);
	}
	
	@Test(groups="dbtest")
	public void testViewRegistrationAllowed()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		RegistrationEntity testUserRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan,
																											UserInfo.Id.TestUser);
		
		RegistrationEntity emailRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan, 
																											UserInfo.Id.Email);
		
		/*this user has CREATOR permissions on the conference, so he can view any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.READ, UserInfo.Users.TestUser);
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.READ, UserInfo.Users.TestUser);
		
		/*this user has UPDATE permissions on the conference, so he can view any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.READ, UserInfo.Users.Ryan);
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.READ, UserInfo.Users.Ryan);
		
		/*this user has VIEW permissions on the conference, so he can view any registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.READ, UserInfo.Users.Email);
		authorizationService.authorizeRegistration(emailRegistration, northernMichiganConference, OperationType.READ, UserInfo.Users.Email);
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testViewRegistrationUnauthorized()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);
		RegistrationEntity testUserRegistration = registrationService.getRegistrationByConferenceIdUserId(ConferenceInfo.Id.NorthernMichigan,
																											UserInfo.Id.TestUser);
		
		/*this user has no permissions on the conference, so he cannot view someone else's registration*/
		authorizationService.authorizeRegistration(testUserRegistration, northernMichiganConference, OperationType.ADMIN, new CrsApplicationUser(UUID.randomUUID(),  null,  null));
	}

	@Test(groups="dbtest")
	public void testNotYetRegisteredAuthorized()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);

		RegistrationEntity registrationEntity =
				registrationService.getRegistrationByConferenceIdUserId(northernMichiganConference.getId(), UserInfo.Id.Ryan);

		/*this user is not yet registered*/
		authorizationService.authorizeRegistration(registrationEntity, northernMichiganConference, OperationType.CREATE, UserInfo.Users.Ryan);
	}

	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testAlreadyRegisteredUnauthorized()
	{
		ConferenceEntity northernMichiganConference = conferenceService.fetchConferenceBy(ConferenceInfo.Id.NorthernMichigan);

		RegistrationEntity registrationEntity =
				registrationService.getRegistrationByConferenceIdUserId(northernMichiganConference.getId(), UserInfo.Id.TestUser);

		/*this user is already registered*/
		authorizationService.authorizeRegistration(registrationEntity, northernMichiganConference, OperationType.CREATE, UserInfo.Users.TestUser);
	}
}
