package org.cru.crs.auth;

import java.util.UUID;

public class CrsApplicationUser implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	UUID id;
	ExternalIdentityAuthenticationProviderAndId externalIdentityDetails;

	public CrsApplicationUser(UUID id, ExternalIdentityAuthenticationProviderAndId externalIdentityDetails)
	{
		this.id = id;
		this.externalIdentityDetails = externalIdentityDetails;
	}
	
	public boolean isCrsAuthenticatedOnly()
	{
		return externalIdentityDetails.getAuthProviderType().equals(AuthenticationProviderType.CRS);
	}
	
	public UUID getId()
	{
		return id;
	}
}
