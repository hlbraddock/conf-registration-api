package org.cru.crs.api.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.joda.time.DateTime;
import org.testng.collections.Lists;

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
		jpaConferenceCosts.setBaseCost(conferenceCost);
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
	 * @param dbConference
	 * @return
	 */
	public static Conference fromDb(ConferenceEntity dbConference, ConferenceCostsEntity dbConferenceCosts)
	{
		Conference webConference = new Conference();
		
		webConference.id = dbConference.getId();
		webConference.name = dbConference.getName();
		webConference.eventStartTime = dbConference.getEventStartTime();
		webConference.eventEndTime = dbConference.getEventEndTime();
		webConference.registrationStartTime = dbConference.getRegistrationStartTime();
		webConference.registrationEndTime = dbConference.getRegistrationEndTime();
		webConference.totalSlots = dbConference.getTotalSlots();
		webConference.contactUser = dbConference.getContactPersonId();
        webConference.contactPersonName = dbConference.getContactPersonName();
        webConference.contactPersonEmail = dbConference.getContactPersonEmail();
        webConference.contactPersonPhone = dbConference.getContactPersonPhone();

        webConference.locationName = dbConference.getLocationName();
        webConference.locationAddress = dbConference.getLocationAddress();
        webConference.locationCity = dbConference.getLocationCity();
        webConference.locationState = dbConference.getLocationState();
        webConference.locationZipCode = dbConference.getLocationZipCode();

        if(dbConferenceCosts != null)
        {
            webConference.authnetId = dbConferenceCosts.getAuthnetId();
            /*don't expose the authnet token back out to the Client!*/
            webConference.acceptCreditCards = dbConferenceCosts.isAcceptCreditCards();
            webConference.conferenceCost = dbConferenceCosts.getBaseCost();
            webConference.minimumDeposit = dbConferenceCosts.getMinimumDeposit();
            webConference.earlyRegistrationAmount = dbConferenceCosts.getEarlyRegistrationAmount();
            webConference.earlyRegistrationCutoff = dbConferenceCosts.getEarlyRegistrationCutoff();
            webConference.earlyRegistrationDiscount = dbConferenceCosts.isEarlyRegistrationDiscount();
        }
        
        webConference.registrationPages = Lists.newArrayList();
        
		return webConference;
	}

	public static Conference fromDb(ConferenceEntity dbConference, ConferenceCostsEntity dbConferenceCosts, List<PageEntity> pages, Map<UUID,List<BlockEntity>> dbBlocks)
	{
		Conference webConference = fromDb(dbConference, dbConferenceCosts);
		
		for(PageEntity page : pages)
		{
			webConference.registrationPages.add(Page.fromDb(page, dbBlocks.get(page.getId())));
		}
		
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
