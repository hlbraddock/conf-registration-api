package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

public class IdentityEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;
	
	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

}
