package org.cru.crs.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "CONFERENCE_COSTS")
public class ConferenceCostsEntity
{
    @Id
    @Column(name = "ID")
    @Type(type = "pg-uuid")
    UUID id;

    @Column(name = "BASE_COST")
    BigDecimal conferenceBaseCost;

    @Column(name = "EARLY_REGISTRATION_DISCOUNT")
    boolean earlyRegistrationDiscount;

    @Column(name = "EARLY_REGISTRATION_AMOUNT")
    BigDecimal earlyRegistrationAmount;

    @Column(name = "EARLY_REGISTRATION_CUTOFF")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime earlyRegistrationCutoff;

    @Column(name = "ACCEPT_CREDIT_CARDS")
    boolean acceptCreditCards;

    @Column(name = "AUTHNET_ID")
    String authnetId;

    @Column(name = "AUTHNET_TOKEN")
    String authnetToken;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public BigDecimal getConferenceBaseCost()
    {
        return conferenceBaseCost;
    }

    public void setConferenceBaseCost(BigDecimal conferenceBaseCost)
    {
        this.conferenceBaseCost = conferenceBaseCost;
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
}