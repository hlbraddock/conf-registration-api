package org.cru.crs.api.process;

import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;

public class UpdatePermissionProcess extends PermissionProcess {
	
	public void updatePermission(Permission existingPermission, CrsApplicationUser crsLoggedInUser) {
		preparePermissionForDatabase(existingPermission, existingPermission.getConferenceId(), crsLoggedInUser);
		permissionService.updatePermission(existingPermission.toDbPermissionEntity());
	}
	
}
