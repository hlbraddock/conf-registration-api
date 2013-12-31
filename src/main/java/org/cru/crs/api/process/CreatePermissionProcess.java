package org.cru.crs.api.process;

import java.util.UUID;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.UserService;

public class CreatePermissionProcess extends PermissionProcess {

	@Inject PermissionService permissionService;
	@Inject UserService userService;
	@Inject Clock clock;
	
	public void savePermission(Permission newPermission, UUID conferenceId, CrsApplicationUser loggedInUser) {
		preparePermissionForDatabase(newPermission, conferenceId, loggedInUser);
		permissionService.insertPermission(newPermission.toDbPermissionEntity());
	}

}
