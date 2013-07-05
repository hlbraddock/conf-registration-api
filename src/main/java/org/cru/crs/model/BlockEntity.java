package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "CRU_CRS_BLOCKS")
public class BlockEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;
	
	@Column(name = "CONFERENCE_ID")
	@Type(type="pg-uuid")
	private UUID conferenceId;
	
	@Column(name = "PAGE_ID")
	@Type(type="pg-uuid")
	private UUID pageId;
	
	@Column(name = "BLOCK_TYPE")
	private String blockType;
	
	@Column(name = "ADMIN_ONLY")
	private boolean adminOnly;
	
	@Column(name = "BLOCK_DESCIPTION")
	private String blockDescription;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getConferenceId()
	{
		return conferenceId;
	}

	public void setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
	}

	public UUID getPageId()
	{
		return pageId;
	}

	public void setPageId(UUID pageId)
	{
		this.pageId = pageId;
	}

	public String getBlockType()
	{
		return blockType;
	}

	public void setBlockType(String blockType)
	{
		this.blockType = blockType;
	}

	public boolean isAdminOnly()
	{
		return adminOnly;
	}

	public void setAdminOnly(boolean adminOnly)
	{
		this.adminOnly = adminOnly;
	}

	public String getBlockDescription()
	{
		return blockDescription;
	}

	public void setBlockDescription(String blockDescription)
	{
		this.blockDescription = blockDescription;
	}
}
