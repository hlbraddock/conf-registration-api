package org.cru.crs.model;

import java.math.BigDecimal;
import java.util.UUID;

import org.joda.time.DateTime;

public class ConferenceCostsEntity
{
    UUID id;

    BigDecimal conferenceBaseCost;

    boolean earlyRegistrationDiscount;
    BigDecimal earlyRegistrationAmount;
    DateTime earlyRegistrationCutoff;
    
    BigDecimal minimumDeposit;

    boolean acceptCreditCards;
    String authnetId;
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
}