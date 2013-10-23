package org.cru.crs.api.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.joda.time.DateTime;

public class Conference implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String name;

    private String description;

	private List<Page> registrationPages;
	
	private DateTime eventStartTime;
	private DateTime eventEndTime;

	private DateTime registrationStartTime;
	private DateTime registrationEndTime;
	private boolean registrationOpen = false;

	private UUID contactUser;
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;

	private int totalSlots;
	
	private String locationName;
    private String locationAddress;
    private String locationCity;
    private String locationState;
    private String locationZipCode;

    private BigDecimal conferenceCost;
    private BigDecimal minimumDeposit;
    private boolean earlyRegistrationDiscount;
    private BigDecimal earlyRegistrationAmount;
    private DateTime earlyRegistrationCutoff;
    private boolean earlyRegistrationOpen = false;
    private boolean acceptCreditCards;
    private String authnetId;
    private String authnetToken;


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
		jpaConference.setContactPersonId(contactUser);
        jpaConference.setContactPersonName(contactPersonName);
        jpaConference.setContactPersonEmail(contactPersonEmail);
        jpaConference.setContactPersonPhone(contactPersonPhone);

        jpaConference.setLocationName(locationName);
        jpaConference.setLocationAddress(locationAddress);
        jpaConference.setLocationCity(locationCity);
        jpaConference.setLocationState(locationState);
        jpaConference.setLocationZipCode(locationZipCode);

		ConferenceCostsEntity jpaConferenceCosts = new ConferenceCostsEntity();
		jpaConferenceCosts.setId(id);
		jpaConferenceCosts.setAcceptCreditCards(acceptCreditCards);
		jpaConferenceCosts.setAuthnetId(authnetId);
		jpaConferenceCosts.setAuthnetToken(authnetToken);
		jpaConferenceCosts.setConferenceBaseCost(conferenceCost);
        jpaConferenceCosts.setMinimumDeposit(minimumDeposit);
		jpaConferenceCosts.setEarlyRegistrationAmount(earlyRegistrationAmount);
		jpaConferenceCosts.setEarlyRegistrationCutoff(earlyRegistrationCutoff);
		jpaConferenceCosts.setEarlyRegistrationDiscount(earlyRegistrationDiscount);

		jpaConference.setConferenceCostsId(jpaConferenceCosts.getId());

		return jpaConference;
	}
	
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
		webConference.contactUser = jpaConference.getContactPersonId();
        webConference.contactPersonName = jpaConference.getContactPersonName();
        webConference.contactPersonEmail = jpaConference.getContactPersonEmail();
        webConference.contactPersonPhone = jpaConference.getContactPersonPhone();

        webConference.locationName = jpaConference.getLocationName();
        webConference.locationAddress = jpaConference.getLocationAddress();
        webConference.locationCity = jpaConference.getLocationCity();
        webConference.locationState = jpaConference.getLocationState();
        webConference.locationZipCode = jpaConference.getLocationZipCode();

        if(jpaConference.getConferenceCostsId() != null)
        {
//            webConference.authnetId = jpaConference.getConferenceCosts().getAuthnetId();
//            /*don't expose the authnet token back out to the Client!*/
//            webConference.acceptCreditCards = jpaConference.getConferenceCosts().isAcceptCreditCards();
//            webConference.conferenceCost = jpaConference.getConferenceCosts().getConferenceBaseCost();
//            webConference.minimumDeposit = jpaConference.getConferenceCosts().getMinimumDeposit();
//            webConference.earlyRegistrationAmount = jpaConference.getConferenceCosts().getEarlyRegistrationAmount();
//            webConference.earlyRegistrationCutoff = jpaConference.getConferenceCosts().getEarlyRegistrationCutoff();
//            webConference.earlyRegistrationDiscount = jpaConference.getConferenceCosts().isEarlyRegistrationDiscount();
        }
		return webConference;
	}

	public static Conference fromJpaWithPages(ConferenceEntity jpaConference)
	{
		Conference webConference = fromJpa(jpaConference);
		
//		webConference.registrationPages = Page.fromJpa(jpaConference.getPages());
		
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

    public int getTotalSlots()
	{
		return totalSlots;
	}

	public void setTotalSlots(int totalSlots)
	{
		this.totalSlots = totalSlots;
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

    public BigDecimal getConferenceCost()
    {
        return conferenceCost;
    }

    public void setConferenceCost(BigDecimal conferenceCost)
    {
        this.conferenceCost = conferenceCost;
    }

    public BigDecimal getMinimumDeposit()
    {
        return minimumDeposit;
    }

    public void setMinimumDeposit(BigDecimal minimumDeposit)
    {
        this.minimumDeposit = minimumDeposit;
    }

    public boolean isEarlyRegistrationDiscount()
    {
        return earlyRegistrationDiscount;
    }

    public void setEarlyRegistrationDiscount(boolean earlyRegistrationDiscount)
    {
        this.earlyRegistrationDiscount = earlyRegistrationDiscount;
    }

    public BigDecimal getEarlyRegistrationAmount()
    {
        return earlyRegistrationAmount;
    }

    public void setEarlyRegistrationAmount(BigDecimal earlyRegistrationAmount)
    {
        this.earlyRegistrationAmount = earlyRegistrationAmount;
    }

    public DateTime getEarlyRegistrationCutoff()
    {
        return earlyRegistrationCutoff;
    }

    public void setEarlyRegistrationCutoff(DateTime earlyRegistrationCutoff)
    {
        this.earlyRegistrationCutoff = earlyRegistrationCutoff;
    }

    public boolean isAcceptCreditCards()
    {
        return acceptCreditCards;
    }

    public void setAcceptCreditCards(boolean acceptCreditCards)
    {
        this.acceptCreditCards = acceptCreditCards;
    }

    public String getAuthnetId()
    {
        return authnetId;
    }

    public void setAuthnetId(String authnetId)
    {
        this.authnetId = authnetId;
    }

    public String getAuthnetToken()
    {
        return authnetToken;
    }

    public void setAuthnetToken(String authnetToken)
    {
        this.authnetToken = authnetToken;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isRegistrationOpen()
    {
        return registrationOpen;
    }

    public void setRegistrationOpen(boolean registrationOpen)
    {
        this.registrationOpen = registrationOpen;
    }

    public boolean isEarlyRegistrationOpen()
    {
        return earlyRegistrationOpen;
    }

    public void setEarlyRegistrationOpen(boolean earlyRegistrationOpen)
    {
        this.earlyRegistrationOpen = earlyRegistrationOpen;
    }
}
