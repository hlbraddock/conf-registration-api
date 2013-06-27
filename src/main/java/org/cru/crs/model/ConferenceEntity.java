package org.cru.crs.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.postgresql.jdbc4.Jdbc4Array;

/**
 * Entity class which represents data about a Cru conference.
 * 
 * @author ryancarlson
 *
 */
@Entity
@Table(name = "CRU_CRS_CONFERENCES")
public class ConferenceEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private java.util.UUID id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "PAGES")
	@Type(type="org.cru.crs.utils.CustomArrayType")
	private Jdbc4Array pages;

	@Column(name = "EVENT_START_TIME")
	private Date eventStartTime;
	
	@Column(name = "EVENT_END_TIME")
	private Date eventEndTime;
	
	@Column(name = "REGISTRATION_START_TIME")
	private Date registrationStartTime;
	
	@Column(name = "REGISTRATION_END_TIME")
	private Date registrationEndTime;
	
	@Column(name = "TOTAL_SLOTS")
	private int totalSlots;
	
	@Column(name = "CONTACT_USER")
	@Type(type="pg-uuid")
	private java.util.UUID contactUser;

	public java.util.UUID getId()
	{
		return id;
	}

	public void setId(java.util.UUID id)
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

	public Jdbc4Array getPages()
	{
		return pages;
	}

	public void setPages(Jdbc4Array pages)
	{
		this.pages = pages;
	}
	
	public Date getEventStartTime()
	{
		return eventStartTime;
	}

	public void setEventStartTime(Date eventStartTime)
	{
		this.eventStartTime = eventStartTime;
	}

	public Date getEventEndTime()
	{
		return eventEndTime;
	}

	public void setEventEndTime(Date eventEndTime)
	{
		this.eventEndTime = eventEndTime;
	}

	public Date getRegistrationStartTime()
	{
		return registrationStartTime;
	}

	public void setRegistrationStartTime(Date registrationStartTime)
	{
		this.registrationStartTime = registrationStartTime;
	}

	public Date getRegistrationEndTime()
	{
		return registrationEndTime;
	}

	public void setRegistrationEndTime(Date registrationEndTime)
	{
		this.registrationEndTime = registrationEndTime;
	}

	public int getTotalSlots()
	{
		return totalSlots;
	}

	public void setTotalSlots(int totalSlots)
	{
		this.totalSlots = totalSlots;
	}

	public java.util.UUID getContactUser()
	{
		return contactUser;
	}

	public void setContactUser(java.util.UUID contactUser)
	{
		this.contactUser = contactUser;
	}
	
}
