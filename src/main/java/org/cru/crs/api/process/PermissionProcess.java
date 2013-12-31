package org.cru.crs.api.process;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.UserService;

public abstract class PermissionProcess {

	@Inject PermissionService permissionService;
	@Inject UserService userService;
	@Inject Clock clock;
	
	protected void preparePermissionForDatabase(Permission permission, UUID conferenceId, CrsApplicationUser crsLoggedInUser) {
		if(permission.getUserId() == null) {
			boolean match = lookupUserByUsername(permission);

			if(!match) {
				match = lookupUserByName(permission);
			}

			if(!match) {
				throw new BadRequestException();
			}
		}
		setPermissionFields(conferenceId, permission, crsLoggedInUser);
	}

	private boolean lookupUserByUsername(Permission newPermission) {		
		UserEntity matchedUser = userService.getUserByEmailAddress(newPermission.getEmailAddress());
		
		if(matchedUser != null) {
			newPermission.setUserId(matchedUser.getId());
			return true;
		}
		
		return false;
	}

	private boolean lookupUserByName(Permission newPermission) {
		UserEntity matchedUser = userService.getUserByFirstAndLastName(newPermission.getFirstName(), newPermission.getLastName());
		
		if(matchedUser != null) {
			newPermission.setUserId(matchedUser.getId());
			return true;
		}
		
		return false;
	}

	private void setPermissionFields(UUID conferenceId, Permission newPermission, CrsApplicationUser crsLoggedInUser) {
		if(newPermission.getId() == null) newPermission.setId(UUID.randomUUID());
		newPermission.setConferenceId(conferenceId);
		newPermission.setGivenByUserId(crsLoggedInUser.getId());
		newPermission.setTimestamp(clock.currentDateTime());
	}
}
