package org.cru.crs.api.process;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.service.PermissionService;

import com.google.common.base.Strings;

public class UpdatePermissionProcess
{
	
	PermissionService permissionService;
	Clock clock;
	
	@Inject
	public UpdatePermissionProcess(PermissionService permissionService, Clock clock)
	{
		this.permissionService = permissionService;
		this.clock = clock;
	}

	
	public void updatePermission(Permission existingPermission, CrsApplicationUser crsLoggedInUser)
	{
		/* 
         * If the user ID is null on the passed in permission and an activation code is present, then this user
         * is activating or "accepting" the granted permission.  Assuming validation passes, set the userId
         * of the logged in user on the permission.  It is then ready to be used by other queries.
		 */
		if(existingPermission.getUserId() == null && !Strings.isNullOrEmpty(existingPermission.getActivationCode()))
		{
			validateActivationCode(existingPermission.getActivationCode());
			existingPermission.setUserId(crsLoggedInUser.getId());
		}
		
		existingPermission.setGivenByUserId(crsLoggedInUser.getId())
							.setTimestamp(clock.currentDateTime());
		
		permissionService.updatePermission(existingPermission.toDbPermissionEntity());
	}
	
	/**
	 * Look for a permission stored with the provided activation code.
	 *  - If there is no permission stored, then result is a BadRequestException (400)
	 *  - If there permission is stored and already has a userId associated with it, then result is a ForbiddenException (403)
	 * @param existingPermission
	 * @return
	 */
	private void validateActivationCode(String activationCode)
	{
		PermissionEntity storedPermission = permissionService.getPermissionByActivationCode(activationCode);
		
		if(storedPermission == null) throw new BadRequestException("invalid activation code");
		
		if(storedPermission.getUserId() != null) throw new ForbiddenException("activation code has already been used");
		
		if(storedPermission.getLastUpdatedTimestamp().plusMonths(Permission.MONTHS_BEFORE_UNACCEPTED_PERMISSION_EXPIRES).isBefore(clock.currentDateTime()))
		{
			throw new ForbiddenException("maximum time has elapsed to accept this permission");
		}
	}
	
}
