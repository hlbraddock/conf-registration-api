package org.cru.crs.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
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
	
	private UUID contactUser;
	private int totalSlots;
	
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
		webConference.totalSlots = jpaConference.getTotalSlots();
		webConference.contactUser = jpaConference.getContactUser();
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
	
	public ConferenceEntity toJpaConferenceEntity()
	{
		ConferenceEntity jpaConference = new ConferenceEntity();
		
		jpaConference.setId(id);
		jpaConference.setName(name);
		jpaConference.setEventStartTime(eventStartTime);
		jpaConference.setEventEndTime(eventEndTime);
		jpaConference.setRegistrationStartTime(registrationStartTime);
		jpaConference.setRegistrationEndTime(registrationEndTime);
		jpaConference.setTotalSlots(totalSlots);
		jpaConference.setContactUser(contactUser);
		
		return jpaConference;
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

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getEventStartTime()
	{
		return eventStartTime;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setEventStartTime(DateTime eventStartTime)
	{
		this.eventStartTime = eventStartTime;
	}

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getEventEndTime()
	{
		return eventEndTime;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setEventEndTime(DateTime eventEndTime)
	{
		this.eventEndTime = eventEndTime;
	}

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getRegistrationStartTime()
	{
		return registrationStartTime;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setRegistrationStartTime(DateTime registrationStartTime)
	{
		this.registrationStartTime = registrationStartTime;
	}

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getRegistrationEndTime()
	{
		return registrationEndTime;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setRegistrationEndTime(DateTime registrationEndTime)
	{
		this.registrationEndTime = registrationEndTime;
	}

	public UUID getContactUser()
	{
		return contactUser;
	}

	public void setContactUser(UUID contactUser)
	{
		this.contactUser = contactUser;
	}

	public int getTotalSlots()
	{
		return totalSlots;
	}

	public void setTotalSlots(int totalSlots)
	{
		this.totalSlots = totalSlots;
	}
	
}
