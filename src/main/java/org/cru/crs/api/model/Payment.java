package org.cru.crs.api.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Payment implements Serializable
{
    private UUID id;
    private UUID registrationId;
    private String creditCardNameOnCard;
    private String creditCardExpirationMonth;
    private String creditCardExpirationYear;
    private String creditCardLastFourDigits;
    private BigDecimal amount;
    private DateTime transactionDatetime;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(UUID registrationId)
    {
        this.registrationId = registrationId;
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

    @JsonSerialize(using=JsonStandardDateTimeSerializer.class)
    public DateTime getTransactionDatetime()
    {
        return transactionDatetime;
    }

    @JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
    public void setTransactionDatetime(DateTime transactionDatetime)
    {
        this.transactionDatetime = transactionDatetime;
    }
}
