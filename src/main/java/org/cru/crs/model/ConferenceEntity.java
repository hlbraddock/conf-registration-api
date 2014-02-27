package org.cru.crs.model;

import java.util.UUID;

import org.joda.time.DateTime;

/**
 * Entity class which represents data about a Cru conference.
 *
 * @author ryancarlson
 *
 */

public class ConferenceEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private UUID conferenceCostsId;
	private String name;
    private String description;

	private DateTime eventStartTime;
	private DateTime eventEndTime;
	private DateTime registrationStartTime;
	private DateTime registrationEndTime;

	private int totalSlots;
	
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;

    private String locationName;
    private String locationAddress;
    private String locationCity;
    private String locationState;
    private String locationZipCode;
    
	private boolean requireLogin;
    private boolean archived =  false;
	
	public UUID getId()
	{
		return id;
	}
	
	public ConferenceEntity setId(UUID id)
	{
		this.id = id;
		return this;
	}
	
	public UUID getConferenceCostsId()
	{
		return conferenceCostsId;
	}
	
	public void setConferenceCostsId(UUID conferenceCostsId)
	{
		this.conferenceCostsId = conferenceCostsId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
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

	public String getContactPersonName()
	{
		return contactPersonName;
	}
	
	public void setContactPersonName(String contactPersonName)
	{
		this.contactPersonName = contactPersonName;
	}
	
	public String getContactPersonEmail()
	{
		return contactPersonEmail;
	}
	
	public void setContactPersonEmail(String contactPersonEmail)
	{
		this.contactPersonEmail = contactPersonEmail;
	}
	
	public String getContactPersonPhone()
	{
		return contactPersonPhone;
	}
	
	public void setContactPersonPhone(String contactPersonPhone)
	{
		this.contactPersonPhone = contactPersonPhone;
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

	public boolean isRequireLogin()
	{
		return requireLogin;
	}

	public void setRequireLogin(boolean requireLogin)
	{
		this.requireLogin = requireLogin;
	}

    public boolean isArchived()
    {
        return archived;
    }

    public void setArchived(boolean archived)
    {
        this.archived = archived;
    }
}
