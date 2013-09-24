package org.cru.crs.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "SESSIONS")
public class SessionEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;

	@Column(name = "AUTH_CODE")
	@Type(type = "pg-uuid")
	private UUID authCode;

	@Column(name = "EXPIRATION")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	DateTime expiration;

	@ManyToOne()
	@JoinColumn(name = "AUTH_PROVIDER_ID", nullable = false, updatable = false)
	private AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(UUID authCode)
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
