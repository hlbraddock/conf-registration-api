package org.cru.crs.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.joda.time.DateTime;

public class PaymentEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;

    private UUID registrationId;

    private Long authnetTransactionId;

    private String ccNameOnCard;

    private String ccExpirationMonth;

    private String ccExpirationYear;

    private String ccLastFourDigits;

    BigDecimal amount;

    private DateTime transactionTimestamp;

    private PaymentType paymentType;
    
    public UUID getId()
    {
        return id;
    }

    public PaymentEntity setId(UUID id)
    {
        this.id = id;
        return this;
    }

    public UUID getRegistrationId()
    {
        return registrationId;
    }

    public PaymentEntity setRegistrationId(UUID registrationId)
    {
        this.registrationId = registrationId;
        return this;
    }

    public Long getAuthnetTransactionId()
    {
        return authnetTransactionId;
    }

    public void setAuthnetTransactionId(Long authnetTransactionId)
    {
        this.authnetTransactionId = authnetTransactionId;
    }

    public String getCcNameOnCard()
    {
        return ccNameOnCard;
    }

    public void setCcNameOnCard(String ccNameOnCard)
    {
        this.ccNameOnCard = ccNameOnCard;
    }

    public String getCcExpirationMonth()
    {
        return ccExpirationMonth;
    }

    public void setCcExpirationMonth(String ccExpirationMonth)
    {
        this.ccExpirationMonth = ccExpirationMonth;
    }

    public String getCcExpirationYear()
    {
        return ccExpirationYear;
    }

    public void setCcExpirationYear(String ccExpirationYear)
    {
        this.ccExpirationYear = ccExpirationYear;
    }

    public String getCcLastFourDigits()
    {
        return ccLastFourDigits;
    }

    public void setCcLastFourDigits(String ccLastFourDigits)
    {
        this.ccLastFourDigits = ccLastFourDigits;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public DateTime getTransactionTimestamp()
    {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(DateTime transactionTimestamp)
    {
        this.transactionTimestamp = transactionTimestamp;
    }

	public PaymentType getPaymentType()
	{
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}
}