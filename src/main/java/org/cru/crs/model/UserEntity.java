package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "CRU_CRS_USERS")
public class UserEntity implements java.io.Serializable
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

	public UserEntity setId(UUID id)
	{
		this.id = id;
		return this;
	}
}
