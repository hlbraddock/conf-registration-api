package org.cru.crs.api.model;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;

public class Page implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID conferenceId;
	
	private String title;
	private int position;
	private List<Block> blocks;
	
	public static Page fromDb(PageEntity dbPage)
	{
		Page page = new Page();
		
		page.id = dbPage.getId();
		page.conferenceId = dbPage.getConferenceId();
		page.title = dbPage.getTitle();
		page.position = dbPage.getPosition();
		
		return page;
	}
	
	public static Page fromDb(PageEntity dbPage, List<BlockEntity> dbBlocks)
	{
		Page page = new Page();
		
		page.id = dbPage.getId();
		page.conferenceId = dbPage.getConferenceId();
		page.title = dbPage.getTitle();
		page.position = dbPage.getPosition();
		page.blocks = Block.fromDb(dbBlocks);
		
		return page;
	}
	
	public PageEntity toDbPageEntity()
	{
		PageEntity dbPage = new PageEntity();
		
		dbPage.setId(id);
		dbPage.setConferenceId(conferenceId);
		dbPage.setTitle(title);
		dbPage.setPosition(position);

		return dbPage;
	}

    public int hashCode()
    {
        return new HashCodeBuilder(29, 79). // two randomly chosen prime numbers
                append(id).
                toHashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Page)) return false;

        Page rhs = (Page) obj;
        return new EqualsBuilder().
                append(id, rhs.id).
                isEquals();
    }

	public UUID getId()
	{
		return id;
	}

	public Page setId(UUID id)
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

	public int getPosition()
	{
		return position;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public List<Block> getBlocks()
	{
		return blocks;
	}

	public void setBlocks(List<Block> blocks)
	{
		this.blocks = blocks;
	}

	public UUID getConferenceId()
	{
		return conferenceId;
	}

	public void setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
	}
}
