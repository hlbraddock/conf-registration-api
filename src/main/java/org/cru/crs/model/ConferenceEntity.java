package org.cru.crs.model;

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
import org.joda.time.DateTime;

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
	private UUID id;
	
	@Column(name = "NAME")
	private String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "CONFERENCE_ID", nullable = false)
    @OrderColumn(name = "POSITION", nullable = false)
	private List<PageEntity> pages;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "CONFERENCE_ID", nullable = false)
    private List<RegistrationEntity> registrations;

    @Column(name = "EVENT_START_TIME")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime eventStartTime;
	
	@Column(name = "EVENT_END_TIME")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime eventEndTime;
	
	@Column(name = "REGISTRATION_START_TIME")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime registrationStartTime;
	
	@Column(name = "REGISTRATION_END_TIME")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime registrationEndTime;
	
	@Column(name = "TOTAL_SLOTS")
	private int totalSlots;
	
	@Column(name = "CONTACT_USER")
	@Type(type="pg-uuid")
	private UUID contactUser;

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

	public DateTime getEventStartTime()
	{
		return eventStartTime;
	}

	public void setEventStartTime(DateTime eventStartTime)
	{
		this.eventStartTime = eventStartTime;
	}

	public DateTime getEventEndTime()
	{
		return eventEndTime;
	}

	public void setEventEndTime(DateTime eventEndTime)
	{
		this.eventEndTime = eventEndTime;
	}

	public DateTime getRegistrationStartTime()
	{
		return registrationStartTime;
	}

	public void setRegistrationStartTime(DateTime registrationStartTime)
	{
		this.registrationStartTime = registrationStartTime;
	}

	public DateTime getRegistrationEndTime()
	{
		return registrationEndTime;
	}

	public void setRegistrationEndTime(DateTime registrationEndTime)
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

	public UUID getContactUser()
	{
		return contactUser;
	}

	public void setContactUser(UUID contactUser)
	{
		this.contactUser = contactUser;
	}

	public List<PageEntity> getPages()
	{
		return pages;
	}

	public void setPages(List<PageEntity> pages)
	{
		this.pages = pages;
	}

	public List<RegistrationEntity> getRegistrations()
	{
		return registrations;
	}

	public void setRegistrations(List<RegistrationEntity> registrations)
	{
		this.registrations = registrations;
	}
}
