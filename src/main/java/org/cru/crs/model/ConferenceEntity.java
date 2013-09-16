package org.cru.crs.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Entity class which represents data about a Cru conference.
 *
 * @author ryancarlson
 *
 */
@Entity
@Table(name = "CONFERENCES")
public class ConferenceEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;

	@Column(name = "NAME")
	private String name;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "CONFERENCE_ID", nullable = false)
    @OrderColumn(name = "POSITION", nullable = false)
	private List<PageEntity> pages;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "CONFERENCE_COSTS_ID")
    ConferenceCostsEntity conferenceCosts;

    @Column(name = "LOCATION_NAME")
    private String locationName;

    @Column(name = "LOCATION_ADDRESS")
    private String locationAddress;

    @Column(name = "LOCATION_CITY")
    private String locationCity;

    @Column(name = "LOCATION_STATE")
    private String locationState;

    @Column(name = "LOCATION_ZIP_CODE")
    private String locationZipCode;

    public UUID getId()
	{
		return id;
	}

	public ConferenceEntity setId(UUID id)
	{
		this.id = id;
		return this;
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

    public String getLocationName()
    {
        return locationName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public String getLocationAddress()
    {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress)
    {
        this.locationAddress = locationAddress;
    }

    public String getLocationCity()
    {
        return locationCity;
    }

    public void setLocationCity(String locationCity)
    {
        this.locationCity = locationCity;
    }

    public String getLocationState()
    {
        return locationState;
    }

    public void setLocationState(String locationState)
    {
        this.locationState = locationState;
    }

    public String getLocationZipCode()
    {
        return locationZipCode;
    }

    public void setLocationZipCode(String locationZipCode)
    {
        this.locationZipCode = locationZipCode;
    }

    public ConferenceCostsEntity getConferenceCosts()
    {
        return conferenceCosts;
    }

    public void setConferenceCosts(ConferenceCostsEntity conferenceCosts)
    {
        this.conferenceCosts = conferenceCosts;
    }
}
