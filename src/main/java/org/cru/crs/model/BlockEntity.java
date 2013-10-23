package org.cru.crs.model;

import java.util.UUID;

import org.codehaus.jackson.JsonNode;

public class BlockEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID pageId;
	private UUID conferenceCostsBlockId;
	private int position;

	private String blockType;
	private boolean adminOnly;
	private boolean required;
	
	private JsonNode content;
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

	public boolean isRequired()
	{
		return required;
	}

	public void setRequired(boolean required)
	{
		this.required = required;
	}

	public UUID getConferenceCostsBlockId() {
		return conferenceCostsBlockId;
	}

	public void setConferenceCostsBlockId(UUID conferenceCostsBlockId) {
		this.conferenceCostsBlockId = conferenceCostsBlockId;
	}

}
