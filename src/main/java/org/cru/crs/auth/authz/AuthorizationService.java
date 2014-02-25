package org.cru.crs.auth.authz;

import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.jaxrs.UnauthorizedException;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.RegistrationService;

public class AuthorizationService
{
	private PermissionService permissionService;
	private RegistrationService registrationService;
	
	public AuthorizationService()
	{
	}

	@Inject
	public AuthorizationService(PermissionService permissionService, RegistrationService registrationService)
	{
		this.permissionService = permissionService;
		this.registrationService = registrationService;
	}

	public void authorizeConference(ConferenceEntity conferenceEntity, OperationType operationType, CrsApplicationUser crsApplicationUser)
	{
		if(conferenceEntity == null) return;

		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}

		if(operationType.equals(OperationType.ADMIN) || operationType.equals(OperationType.DELETE))
		{
			if(!userCanAdministerConference(conferenceEntity.getId(),crsApplicationUser.getId())) throw new UnauthorizedException();
		}
		
		if(operationType.equals(OperationType.UPDATE))
		{
			if(!userCanUpdateConference(conferenceEntity.getId(),crsApplicationUser.getId())) throw new UnauthorizedException();
		}
		
		/*Read is implied to be okay */
	}

	public void authorizeRegistration(RegistrationEntity registrationEntity, ConferenceEntity conferenceEntity, OperationType operationType, CrsApplicationUser crsApplicationUser)
	{
		UUID conferenceId = conferenceEntity.getId();

		if(registrationEntity == null) return;

		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}

		if(operationType.equals(OperationType.CREATE))
		{
			if(conferenceEntity.isRequireLogin() && crsApplicationUser.getAuthProviderType().equals(AuthenticationProviderType.NONE)) throw new UnauthorizedException();

			if (registrationService.isUserRegistered(conferenceId, crsApplicationUser.getId()))	throw new UnauthorizedException();
		}
		if(operationType.equals(OperationType.READ))
		{
			if(!userOwnsRegistration(registrationEntity, crsApplicationUser.getId()) &&
					!userCanViewConferenceRegistrationData(conferenceId, crsApplicationUser.getId())) throw new UnauthorizedException();
		}
		if(OperationType.CUD.contains(operationType))
		{
			if(!userOwnsRegistration(registrationEntity, crsApplicationUser.getId()) &&
					!userCanUpdateConference(conferenceId, crsApplicationUser.getId())) throw new UnauthorizedException();
		}
		if(operationType.equals(OperationType.ADMIN))
		{
			if(!userCanAdministerConference(conferenceId, crsApplicationUser.getId())) throw new UnauthorizedException();
		}
	}

	private boolean userOwnsRegistration(RegistrationEntity registrationEntity, UUID userId)
	{
		return registrationEntity.getUserId().equals(userId);
	}

	private boolean userCanAdministerConference(UUID conferenceId, UUID userId)
	{
		PermissionEntity permission = permissionService.getPermissionForUserOnConference(userId, conferenceId);
		
		return permission != null && permission.getPermissionLevel().isAdminOrAbove();
	}

	private boolean userCanUpdateConference(UUID conferenceId, UUID userId)
	{
		PermissionEntity permission = permissionService.getPermissionForUserOnConference(userId, conferenceId);

		return permission != null && permission.getPermissionLevel().isUpdateOrAbove();
	}

	private boolean userCanViewConferenceRegistrationData(UUID conferenceId, UUID userId)
	{
		PermissionEntity permission = permissionService.getPermissionForUserOnConference(userId, conferenceId);

		return permission != null && permission.getPermissionLevel().isViewOrAbove();
	}
}
