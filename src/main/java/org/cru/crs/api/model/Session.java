package org.cru.crs.api.model;

import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.joda.time.DateTime;

import java.util.UUID;

public class Session implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private String authCode;
	private DateTime expiration;
	private AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity;

	public static Session fromJpa(SessionEntity jpaBlock)
	{
		Session session = new Session();
		
		session.id = jpaBlock.getId();
		session.authCode = jpaBlock.getAuthCode();
		session.expiration = jpaBlock.getExpiration();
//		session.authenticationProviderIdentityEntity = jpaBlock.getAuthenticationProviderIdentityEntity();

		return session;
	}

	public boolean isExpired()
	{
		return expiration.isBefore(new DateTime());
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public DateTime getExpiration()
	{
		return expiration;
	}

	public void setExpiration(DateTime expiration)
	{
		this.expiration = expiration;
	}

	public AuthenticationProviderIdentityEntity getAuthenticationProviderIdentityEntity()
	{
		return authenticationProviderIdentityEntity;
	}

	public void setAuthenticationProviderIdentityEntity(AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity)
	{
		this.authenticationProviderIdentityEntity = authenticationProviderIdentityEntity;
	}
}
