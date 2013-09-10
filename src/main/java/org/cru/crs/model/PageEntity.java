package org.cru.crs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "PAGES")
public class PageEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "CONFERENCE_ID", insertable = false, updatable = false)
	@Type(type="pg-uuid")
	private UUID conferenceId;
	
	@Column(name = "POSITION", insertable = false, updatable = false)
	private int position;

	@OneToMany(cascade =CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "page_id", nullable = false)
    @OrderColumn(name = "position", nullable = false)
	private List<BlockEntity> blocks = new ArrayList<BlockEntity>();

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
	
	public List<BlockEntity> getBlocks()
	{
		return blocks;
	}

	public void setBlocks(List<BlockEntity> blocks)
	{
		this.blocks = blocks;
	}
	
}
