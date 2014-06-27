package org.cru.crs.api.process;

import ch.lambdaj.function.matcher.Predicate;
import org.cru.crs.api.model.Permission;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.service.PermissionService;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.UUID;

import static ch.lambdaj.Lambda.filter;

/**
 * Created by ryancarlson on 6/26/14.
 */
public abstract class PermissionProcess
{
	protected PermissionService permissionService;

	/**
	 * Returns true if there is admin or above permission for the conference other than the one that is passed in as a parameter.
	 *
	 * @param updatedPermission
	 * @return
	 */
	protected boolean doesConferenceHaveOtherFullOrCreatorPermissions(Permission updatedPermission)
	{
		List<PermissionEntity> permissions = filter(adminOrAbove(), permissionService.getPermissionsForConference(updatedPermission.getConferenceId()));

		List<PermissionEntity> adminPermissions = filter(adminOrAbove(), permissions);

		List<PermissionEntity> otherPermissions = filter(otherThan(updatedPermission.getUserId()), adminPermissions);

		return !otherPermissions.isEmpty();
	}

	public Matcher<PermissionEntity> adminOrAbove()
	{
		return new Predicate<PermissionEntity>()
		{
			public boolean apply(PermissionEntity permissionEntity)
			{
				return permissionEntity.getPermissionLevel().isAdminOrAbove();
			}
		};
	}

	public Matcher<PermissionEntity> otherThan(final UUID userId)
	{
		return new Predicate<PermissionEntity>()
		{
			public boolean apply(PermissionEntity permissionEntity)
			{
				return !permissionEntity.getUserId().equals(userId);
			}
		};
	}
}
