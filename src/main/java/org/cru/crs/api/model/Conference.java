package org.cru.crs.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.cru.crs.model.ConferenceEntity;
import org.joda.time.DateTime;

public class Conference implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String name;
	
	private List<Page> registrationPages;
	private DateTime eventStartTime;
	private DateTime eventEndTime;
	
	private DateTime registrationStartTime;
	private DateTime registrationEndTime;
	
	/**
	 * Creates a web api friendly conference, with no pages attached to it.
	 * 
	 * @param jpaConference
	 * @return
	 */
	public static Conference fromJpa(ConferenceEntity jpaConference)
	{
		Conference webConference = new Conference();
		
		webConference.id = jpaConference.getId();
		webConference.name = jpaConference.getName();
		webConference.eventStartTime = jpaConference.getEventStartTime();
		webConference.eventEndTime = jpaConference.getEventEndTime();
		webConference.registrationStartTime = jpaConference.getRegistrationStartTime();
		webConference.registrationEndTime = jpaConference.getRegistrationEndTime();
		
		return webConference;
	}
	
	public static List<Conference> fromJpa(List<ConferenceEntity> jpaConferences)
	{
		List<Conference> conferences = new ArrayList<Conference>();
		
		for(ConferenceEntity jpaConference : jpaConferences)
		{
			conferences.add(fromJpa(jpaConference));
		}
		
		return conferences;
	}
	
	/**
	 * Creates a web api friendly conference, with no pages attached to it.
	 * 
	 * @param jpaConference
	 * @return
	 */
	public static Conference fromJpaWithPages(ConferenceEntity jpaConference)
	{
		Conference webConference = fromJpa(jpaConference);
		
		webConference.registrationPages = Page.fromJpa(jpaConference.getPages());
		
		return webConference;
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

	public List<Page> getRegistrationPages()
	{
		return registrationPages;
	}

	public void setRegistrationPages(List<Page> registrationPages)
	{
		this.registrationPages = registrationPages;
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
	
}
