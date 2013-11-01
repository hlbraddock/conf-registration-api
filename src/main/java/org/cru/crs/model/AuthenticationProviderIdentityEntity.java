package org.cru.crs.model;

import java.util.UUID;

public class AuthenticationProviderIdentityEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private UUID crsId;
	
	/**
	 * This field cannot be a UUID, because not all providers store ID's that way.
	 * Facebook is a prime example of one that does not.. they just use numeric strings
	 */
	private String userAuthProviderId;

	private String authProviderUserAccessToken;
	private String authProviderName;

	private String username;
	private String firstName;	
	private String lastName;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getCrsId() {
		return crsId;
	}
	public void setCrsId(UUID crsId) {
		this.crsId = crsId;
	}
	public String getUserAuthProviderId() {
		return userAuthProviderId;
	}
	public void setUserAuthProviderId(String userAuthProviderId) {
		this.userAuthProviderId = userAuthProviderId;
	}
	public String getAuthProviderUserAccessToken() {
		return authProviderUserAccessToken;
	}
	public void setAuthProviderUserAccessToken(String authProviderUserAccessToken) {
		this.authProviderUserAccessToken = authProviderUserAccessToken;
	}
	public String getAuthProviderName() {
		return authProviderName;
	}
	public void setAuthProviderName(String authProviderName) {
		this.authProviderName = authProviderName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}
