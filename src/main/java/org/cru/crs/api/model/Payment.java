package org.cru.crs.api.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.utils.AuthCodeGenerator;
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
    private String creditCardNumber;
    private BigDecimal amount;
    private DateTime transactionDatetime;

    public PaymentEntity toJpaPaymentEntity()
    {
        PaymentEntity jpaPayment = new PaymentEntity();

        jpaPayment.setId(id);
        jpaPayment.setRegistrationId(registrationId);
        jpaPayment.setAmount(amount);
        jpaPayment.setCreditCardExpirationMonth(creditCardExpirationMonth);
        jpaPayment.setCreditCardExpirationYear(creditCardExpirationYear);
        jpaPayment.setCreditCardNameOnCard(creditCardNameOnCard);

        if(creditCardNumber != null)
        {
            jpaPayment.setCreditCardLastFourDigits(creditCardNumber.substring(Math.max(0, creditCardNumber.length() - 4)));
        }

        //just put something mock in there for now
        jpaPayment.setAuthnetTransactionId(AuthCodeGenerator.generate());

        return jpaPayment;
    }

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

    public String getCreditCardNumber()
    {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber)
    {
        this.creditCardNumber = creditCardNumber;
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
