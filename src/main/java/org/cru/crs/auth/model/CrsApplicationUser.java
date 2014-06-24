package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

import java.util.UUID;

public class CrsApplicationUser implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The User ID for the CRS application, assigned by CRS.  This user ID is used to identify
	 * conferences and registrations belonging to the user who is logged in. */
	UUID id;
	/**
	 * An easy way to identify the authentication provider the user used to identify him/herself*/
	AuthenticationProviderType authProviderType;
	/**
	 * The Username for the user in either the external provider or the email address provided*/
	String authProviderUsername;
	
	public CrsApplicationUser(UUID id, AuthenticationProviderType authProviderType, String authProviderUsername)
	{
		this.id = id;
		this.authProviderType = authProviderType;
		this.authProviderUsername = authProviderUsername;
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

	public AuthenticationProviderType getAuthProviderType()
	{
		return authProviderType;
	}

	public String getAuthProviderUsername()
	{
		return authProviderUsername;
	}
	
}
