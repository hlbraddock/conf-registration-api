package org.cru.crs.api.process;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.service.PermissionService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;

public class UpdatePermissionProcess extends PermissionProcess
{
	Clock clock;
	
	@Inject
	public UpdatePermissionProcess(PermissionService permissionService, Clock clock)
	{
		this.permissionService = permissionService;
		this.clock = clock;
	}

	
	public void updatePermission(Permission updatedPermission)
	{
		boolean conferenceHasAtLeastOneOtherFullOrCreatorPermission = doesConferenceHaveOtherFullOrCreatorPermissions(updatedPermission);

		//If the conference has a full or creator permission, or this one is a full or creator, then we can update
		if(updatedPermission.getPermissionLevel().isAdminOrAbove() || conferenceHasAtLeastOneOtherFullOrCreatorPermission)
		{
			updatedPermission.setTimestamp(clock.currentDateTime());

			permissionService.updatePermission(updatedPermission.toDbPermissionEntity());
		}
		//otherwise this conference will not have a full or creator permission and that's no good.
		else
		{
			throw new BadRequestException("must have at least one creator or full permission.");
		}
	}

	public void acceptPermission(CrsApplicationUser crsLoggedInUser, String activationCode)
	{
		PermissionEntity storedPermission = permissionService.getPermissionByActivationCode(activationCode);
		/* 
		 * If the user ID is null on the passed in permission and an activation code is present, then this user
		 * is activating or "accepting" the granted permission.  Assuming validation passes, set the userId
		 * of the logged in user on the permission.  It is then ready to be used by other queries.
		 */
		validateActivationCode(storedPermission);
		
		storedPermission.setUserId(crsLoggedInUser.getId());
		storedPermission.setLastUpdatedTimestamp(clock.currentDateTime());

		permissionService.updatePermission(storedPermission);
	}

	/**
	 * Look for a permission stored with the provided activation code.
	 *  - If there is no permission stored, then result is a NotFoundException (404)
	 *  - If there permission is stored and already has a userId associated with it, then result is a ForbiddenException (403)
	 * @param storedPermission
	 * @return
	 */
	private void validateActivationCode(PermissionEntity storedPermission)
	{
		if(storedPermission == null) throw new NotFoundException("invalid activation code");
		
		if(storedPermission.getUserId() != null) throw new ForbiddenException("activation code has already been used");
		
		if(storedPermission.getLastUpdatedTimestamp().plusMonths(Permission.MONTHS_BEFORE_UNACCEPTED_PERMISSION_EXPIRES).isBefore(clock.currentDateTime()))
		{
			throw new ForbiddenException("maximum time has elapsed to accept this permission");
		}
	}
	
}
