package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonNode;
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
	
	@Column(name = "PAGE_ID", insertable = false, updatable = false)
	@Type(type="pg-uuid")
	private UUID pageId;
	
	@Column(name = "POSITION", insertable = false, updatable = false)
	private int position;

	@Column(name = "BLOCK_TYPE")
	private String blockType;
	
	@Column(name = "ADMIN_ONLY")
	private boolean adminOnly;
	
	@Column(name = "CONTENT")
	@Type(type="org.cru.crs.utils.JsonUserType")
	private JsonNode content;
	
	@Column(name = "TITLE")
	private String title;

	public UUID getId()
	{
		return id;
	}

	public BlockEntity setId(UUID id)
	{
		this.id = id;
		return this;
	}

	public UUID getPageId()
	{
		return pageId;
	}

	public BlockEntity setPageId(UUID pageId)
	{
		this.pageId = pageId;
		return this;
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

	public JsonNode getContent()
	{
		return content;
	}

	public void setContent(JsonNode content)
	{
		this.content = content;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	
	public int getPosition()
	{
		return position;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}
}
