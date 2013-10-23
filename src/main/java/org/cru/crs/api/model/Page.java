package org.cru.crs.api.model;

import java.util.ArrayList;
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
	
	public static Page fromJpa(PageEntity jpaPage)
	{
		Page page = new Page();
		
		page.id = jpaPage.getId();
		page.conferenceId = jpaPage.getConferenceId();
		page.title = jpaPage.getTitle();
		page.position = jpaPage.getPosition();
//		page.blocks = Block.fromJpa(jpaPage.getBlocks());
		
		return page;
	}
	
	public static List<Page> fromJpa(List<PageEntity> jpaPages)
	{
		List<Page> pages = new ArrayList<Page>();
		
		if(jpaPages == null) return pages;
		
		for(PageEntity jpaPage : jpaPages)
		{
            if(jpaPage == null) continue;
			pages.add(fromJpa(jpaPage));
		}
		
		return pages;
	}
	
	public PageEntity toJpaPageEntity()
	{
		PageEntity jpaPage = new PageEntity();
		
		jpaPage.setId(id);
		jpaPage.setConferenceId(conferenceId);
		jpaPage.setTitle(title);
		jpaPage.setPosition(position);
//		jpaPage.setBlocks(new ArrayList<BlockEntity>());
		
		if(blocks != null)
		{
			for(Block block : blocks)
			{
//				jpaPage.getBlocks().add(block.toJpaBlockEntity());
			}
		}			
		return jpaPage;
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
