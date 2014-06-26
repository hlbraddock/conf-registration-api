package org.cru.crs.api.process;

import org.cru.crs.api.model.Permission;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

/**
 * Created by ryancarlson on 6/26/14.
 */
public class DeletePermissionProcess extends PermissionProcess
{

	public void deletePermission(UUID permissionId)
	{
		if(doesConferenceHaveOtherFullOrCreatorPermissions(Permission.fromDb(permissionService.getPermissionBy(permissionId))))
		{
			permissionService.deletePermission(permissionId);
		}
		else
		{
			throw new BadRequestException("must have at least one creator or full permission.");
		}
	}
}
