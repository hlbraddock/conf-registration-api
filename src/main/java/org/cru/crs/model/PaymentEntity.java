package org.cru.crs.model;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class PaymentEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    private UUID id;

    private UUID registrationId;

    private Long authnetTransactionId;

    private String creditCardNameOnCard;

    private String creditCardExpirationMonth;

    private String creditCardExpirationYear;

    private String creditCardLastFourDigits;

    BigDecimal amount;

    private DateTime transactionDatetime;

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

    public void setRegistrationId(UUID registrationId)
    {
        this.registrationId = registrationId;
    }

    public Long getAuthnetTransactionId()
    {
        return authnetTransactionId;
    }

    public void setAuthnetTransactionId(Long authnetTransactionId)
    {
        this.authnetTransactionId = authnetTransactionId;
    }

    public String getCreditCardNameOnCard()
    {
        return creditCardNameOnCard;
    }

    public void setCreditCardNameOnCard(String creditCardNameOnCard)
    {
        this.creditCardNameOnCard = creditCardNameOnCard;
    }

    public String getCreditCardExpirationMonth()
    {
        return creditCardExpirationMonth;
    }

    public void setCreditCardExpirationMonth(String creditCardExpirationMonth)
    {
        this.creditCardExpirationMonth = creditCardExpirationMonth;
    }

    public String getCreditCardExpirationYear()
    {
        return creditCardExpirationYear;
    }

    public void setCreditCardExpirationYear(String creditCardExpirationYear)
    {
        this.creditCardExpirationYear = creditCardExpirationYear;
    }

    public String getCreditCardLastFourDigits()
    {
        return creditCardLastFourDigits;
    }

    public void setCreditCardLastFourDigits(String creditCardLastFourDigits)
    {
        this.creditCardLastFourDigits = creditCardLastFourDigits;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public DateTime getTransactionDatetime()
    {
        return transactionDatetime;
    }

    public void setTransactionDatetime(DateTime transactionDatetime)
    {
        this.transactionDatetime = transactionDatetime;
    }
}