package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public abstract class AuthenticationProviderUser
{
	protected String id;
	protected String firstName;
	protected String lastName;
	protected String username;
	protected String accessToken;
	protected AuthenticationProviderType authenticationProviderType;
	
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
	public AuthenticationProviderType getAuthenticationProviderType()
	{
		return authenticationProviderType;
	}
	public String getAccessToken()
	{
		return accessToken;
	}
}
