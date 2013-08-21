package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "CRU_CRS_AUTH_PROVIDER_IDENTITIES")
public class AuthenticationProviderIdentityEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * This is the ID used to maintain the join between these Auth Provider IDs and the CRS User ID
	 * This row is not used for much else...
	 */
	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;

	@ManyToOne()
	@JoinColumn(name = "CRS_APP_ID", nullable = false, updatable = false)
	private UserEntity crsUser;
	
	/**
	 * This field cannot be a UUID, because not all providers store ID's that way.
	 * Facebook is a prime example of one that does not.. they just use numeric strings
	 */
	@Column(name = "AUTH_PROVIDER_ID")
	private String authenticationProviderId;
	
	@Column(name = "AUTH_PROVIDER_NAME")
	private String authenticationProviderName;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getAuthenticationProviderId()
	{
		return authenticationProviderId;
	}

	public void setAuthenticationProviderId(String authenticationProviderId)
	{
		this.authenticationProviderId = authenticationProviderId;
	}

	public String getAuthenticationProviderName()
	{
		return authenticationProviderName;
	}

	public void setAuthenticationProviderName(String authenticationProviderName)
	{
		this.authenticationProviderName = authenticationProviderName;
	}

	public UserEntity getCrsUser()
	{
		return crsUser;
	}

	public void setCrsUser(UserEntity crsUser) 
	{
		this.crsUser = crsUser;
	}
}
