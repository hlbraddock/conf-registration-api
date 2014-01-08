package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.UserEntity;

import java.util.UUID;

public abstract class AuthenticationProviderUser
{
	protected String id;
	protected String firstName;
	protected String lastName;
	protected String username;
	protected String accessToken;
	protected AuthenticationProviderType authenticationProviderType;

	public AuthenticationProviderIdentityEntity toAuthProviderIdentityEntity(UUID crsId)
	{
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = new AuthenticationProviderIdentityEntity();

		authProviderIdentityEntity.setId(UUID.randomUUID());
		authProviderIdentityEntity.setCrsId(crsId);
		authProviderIdentityEntity.setUserAuthProviderId(getId());
		authProviderIdentityEntity.setAuthProviderUserAccessToken(getAccessToken());
		authProviderIdentityEntity.setAuthProviderName(getAuthenticationProviderType().name());
		authProviderIdentityEntity.setUsername(getUsername());
		authProviderIdentityEntity.setFirstName(getFirstName());
		authProviderIdentityEntity.setLastName(getLastName());

		return authProviderIdentityEntity;
	}

	public UserEntity toUserEntity()
	{
		UserEntity userEntity = new UserEntity();

		if(getAuthenticationProviderType() == AuthenticationProviderType.RELAY)
			userEntity.setEmailAddress(getUsername());

		userEntity.setFirstName(getFirstName());

		userEntity.setLastName(getLastName());

		return userEntity;
	}

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
	public void setId(String id)
	{
		this.id = id;
	}
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	public void setLastName(String lastName) 
	{
		this.lastName = lastName;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}
	public void setAuthenticationProviderType(AuthenticationProviderType authenticationProviderType)
	{
		this.authenticationProviderType = authenticationProviderType;
	}
	
}
