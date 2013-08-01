package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

public class ExternalIdentityEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;
	
	@Column(name = "CRS_APP_ID")
	@Type(type="pg-uuid")
	private UUID crsApplicationUserId;

	/**
	 * This field cannot be a UUID, because not all providers store ID's that way.
	 * Facebook is a prime example of one that does not.. they just use numeric strings
	 */
	@Column(name = "EXTERNAL_ID")
	private String idFromExternalIdentityProvider;
	
	@Column(name = "EXTERNAL_ID_PROVIDER_NAME")
	private String externalIdentityProviderName;
	
	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getCrsApplicationUserId()
	{
		return crsApplicationUserId;
	}

	public void setCrsApplicationUserId(UUID crsApplicationUserId)
	{
		this.crsApplicationUserId = crsApplicationUserId;
	}

	public String getIdFromExternalIdentityProvider()
	{
		return idFromExternalIdentityProvider;
	}

	public void setIdFromExternalIdentityProvider(String idFromExternalIdentityProvider)
	{
		this.idFromExternalIdentityProvider = idFromExternalIdentityProvider;
	}

	public String getExternalIdentityProviderName()
	{
		return externalIdentityProviderName;
	}

	public void setExternalIdentityProviderName(String externalIdentityProviderName)
	{
		this.externalIdentityProviderName = externalIdentityProviderName;
	}
}
