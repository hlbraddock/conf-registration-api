package org.cru.crs.model;

public enum PermissionLevel
{
	NONE, VIEW, UPDATE, FULL, CREATOR;
	
	public boolean isAdminOrAbove()
	{
		return this == FULL || this == CREATOR;
	}
	
	public boolean isUpdateOrAbove()
	{
		return this == UPDATE || isAdminOrAbove();
	}
	
	public boolean isViewOrAbove()
	{
		return this == VIEW || isUpdateOrAbove();
	}
}
