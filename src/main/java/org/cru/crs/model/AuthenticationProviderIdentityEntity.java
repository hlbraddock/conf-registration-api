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
	
	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;

	@ManyToOne()
	@JoinColumn(name = "CRS_ID", nullable = false, updatable = false)
	private UserEntity crsUser;
	
	/**
	 * This field cannot be a UUID, because not all providers store ID's that way.
	 * Facebook is a prime example of one that does not.. they just use numeric strings
	 */
	@Column(name = "USER_AUTH_PROVIDER_ID")
	private String userAuthProviderId;
	
	@Column(name = "AUTH_PROVIDER_NAME")
	private String authenticationProviderName;

	@Column(name = "USERNAME")
	private String username;
	
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getUserAuthProviderId()
	{
		return userAuthProviderId;
	}

	public void setUserAuthProviderId(String userAuthProviderId)
	{
		this.userAuthProviderId = userAuthProviderId;
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

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}
