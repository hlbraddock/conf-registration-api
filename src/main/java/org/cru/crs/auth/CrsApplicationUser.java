package org.cru.crs.auth;

import java.util.UUID;

public class CrsApplicationUser implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The User ID for the CRS application, assigned by CRS.  This user ID is used to identify
	 * conferences and registrations belonging to the user who is logged in. */
	UUID id;
	/**
	 * The ID of the user in the authentication provider he/she used to identify him/herself.  For
	 * example, this could be the Relay SSO GUID, Facebook ID, or email account ID (associated by CRS)
	 */
	String authProviderId;
	/**
	 * An easy way to identify the authentication provider the user used to identify him/herself*/
	AuthenticationProviderType authProviderType;

	public CrsApplicationUser(UUID id, String authProviderId, AuthenticationProviderType authProviderType)
	{
		this.id = id;
		this.authProviderId = authProviderId;
		this.authProviderType = authProviderType;
	}
	
	/**
	 * Returns true if the user has only authenticated by providing his or her email address to CRS.  If
	 * Facebook or Relay was used, then this method returns false.
	 * @return
	 */
	public boolean isCrsAuthenticatedOnly()
	{
		return authProviderType.equals(AuthenticationProviderType.CRS);
	}
	
	/**
	 * Gets the user ID used to identify this user across this system (CRS).  Note this is different from the
	 * Relay SSO Guid, Facebook ID, or email account ID (even though the email account ID was assigned by this
	 * app)
	 * @return
	 */
	public UUID getId()
	{
		return id;
	}
}
