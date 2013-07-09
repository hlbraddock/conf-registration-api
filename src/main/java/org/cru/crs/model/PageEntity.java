package org.cru.crs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "CRU_CRS_PAGES")
public class PageEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "CONFERENCE_ID", insertable = false, updatable = false)
	@Type(type="pg-uuid")
	private UUID conferenceId;
	
	@Column(name = "POSITION", insertable = false, updatable = false)
	private int position;

	@OneToMany(cascade =CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id", nullable = false)
    @OrderColumn(name = "position", nullable = false)
	private List<BlockEntity> blocks = new ArrayList<BlockEntity>();

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
	
	public List<BlockEntity> getBlocks()
	{
		return blocks;
	}

	public void setBlocks(List<BlockEntity> blocks)
	{
		this.blocks = blocks;
	}
	
}
