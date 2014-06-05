package org.cru.crs.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.google.common.base.Strings;
import org.joda.time.DateTime;

public class ConferenceCostsEntity
{
    UUID id;

    BigDecimal baseCost;

    boolean earlyRegistrationDiscount;
    BigDecimal earlyRegistrationAmount;
    DateTime earlyRegistrationCutoff;
    
    BigDecimal minimumDeposit;

    boolean acceptCreditCards;
    String authnetId;
    String authnetToken;

	public boolean isAbleToProcessPayment()
	{
		return !(Strings.isNullOrEmpty(authnetId) || Strings.isNullOrEmpty(authnetToken));
	}

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public BigDecimal getBaseCost()
    {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost)
    {
        this.baseCost = baseCost;
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