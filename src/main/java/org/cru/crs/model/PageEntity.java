package org.cru.crs.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.UUID;

public class PageEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String title;
	private UUID conferenceId;
	private int position;

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(29, 79). // two randomly chosen prime numbers
                append(id).
                toHashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof PageEntity)) return false;

        PageEntity rhs = (PageEntity) obj;
        return new EqualsBuilder().
                append(id, rhs.id).
                isEquals();
    }
	
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
