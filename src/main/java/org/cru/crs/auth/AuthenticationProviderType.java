package org.cru.crs.auth;

public enum AuthenticationProviderType
{
	RELAY("relaySsoGuid"),
	FACEBOOK("facebookUser"),
	EMAIL_ACCOUNT("crsAppUserId");
	
	AuthenticationProviderType(String sessionIdentifierName)
	{
		this.sessionIdentifierName = sessionIdentifierName;
	}
	
	final String sessionIdentifierName;

	public String getSessionIdentifierName()
	{
		return sessionIdentifierName;
	}
}
