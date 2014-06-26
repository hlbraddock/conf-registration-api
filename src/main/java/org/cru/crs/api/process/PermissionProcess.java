package org.cru.crs.api.process;

import org.cru.crs.api.model.Permission;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.service.PermissionService;

import java.util.List;

/**
 * Created by ryancarlson on 6/26/14.
 */
public abstract class PermissionProcess
{
	protected PermissionService permissionService;

	/**
	 * Loops through all existing permissions for this conference and returns true iff there is a full or creator
	 * permission for the conference other than the one that is passed in as a parameter.
	 *
	 * @param updatedPermission
	 * @return
	 */
	protected boolean doesConferenceHaveOtherFullOrCreatorPermissions(Permission updatedPermission)
	{
		List<PermissionEntity> existingPermissions = permissionService.getPermissionsForConference(updatedPermission.getConferenceId());

		boolean conferenceHasAtLeastOneFullOrCreatorPermission = false;

		for(PermissionEntity existingPermission : existingPermissions)
		{
			if(existingPermission.getPermissionLevel().isAdminOrAbove() && !existingPermission.getId().equals(updatedPermission.getId()))
			{
				conferenceHasAtLeastOneFullOrCreatorPermission = true;
			}
		}
		return conferenceHasAtLeastOneFullOrCreatorPermission;
	}
}
