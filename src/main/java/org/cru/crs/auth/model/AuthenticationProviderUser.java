package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public abstract class AuthenticationProviderUser
{
	protected String id;
	protected String firstName;
	protected String lastName;
	protected String username;
	protected AuthenticationProviderType authentcationProviderType;
	
	public String getId()
	{
		return id;
	}
	public String getFirstName()
	{
		return firstName;
	}
	public String getLastName()
	{
		return lastName;
	}
	public String getUsername()
	{
		return username;
	}
	public AuthenticationProviderType getAuthentcationProviderType()
	{
		return authentcationProviderType;
	}
}
