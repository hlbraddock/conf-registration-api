package org.cru.crs.api.model;

import java.util.UUID;

public class Block implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private String title;
	private String type;
	private String content;
	
	public UUID getId()
	{
		return id;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
}
