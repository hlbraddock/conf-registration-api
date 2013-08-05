package org.cru.crs.auth;

public class ExternalIdentityAuthenticationProviderAndId
{
	AuthenticationProviderType authProviderType;
	String authProviderId;
	
	public ExternalIdentityAuthenticationProviderAndId(AuthenticationProviderType authProviderType, String authProviderId)
	{
		this.authProviderType = authProviderType;
		this.authProviderId = authProviderId;
	}

	public AuthenticationProviderType getAuthProviderType()
	{
		return authProviderType;
	}

	public void setAuthProviderType(AuthenticationProviderType authProviderType)
	{
		this.authProviderType = authProviderType;
	}

	public String getAuthProviderId()
	{
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId)
	{
		this.authProviderId = authProviderId;
	}
}
