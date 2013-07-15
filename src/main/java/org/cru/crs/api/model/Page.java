package org.cru.crs.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.cru.crs.model.PageEntity;

public class Page implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String name;
	private UUID conferenceId;
	
	private int position;
	private List<Block> blocks;
	
	public static Page fromJpa(PageEntity jpaPage)
	{
		Page page = new Page();
		
		page.id = jpaPage.getId();
		page.name = jpaPage.getName();
		page.conferenceId = jpaPage.getConferenceId();
		page.position = jpaPage.getPosition();
//		page.blocks = jpaPage.
		
		return page;
	}
	
	public static List<Page> fromJpa(List<PageEntity> jpaPages)
	{
		return new ArrayList<Page>();
	}
	
	public PageEntity toJpaPageEntity()
	{
		PageEntity jpaPage = new PageEntity();
		
		jpaPage.setId(id);
		
		return jpaPage;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public UUID getConferenceId()
	{
		return conferenceId;
	}

	public void setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
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
}
