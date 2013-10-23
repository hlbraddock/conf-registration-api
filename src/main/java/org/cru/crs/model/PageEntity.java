package org.cru.crs.model;

import java.util.UUID;

public class PageEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String title;
	private UUID conferenceId;
	private int position;

	public UUID getId()
	{
		return id;
	}

	public PageEntity setId(UUID id)
	{
		this.id = id;
		return this;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public UUID getConferenceId()
	{
		return conferenceId;
	}

	public PageEntity setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
		return this;
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
