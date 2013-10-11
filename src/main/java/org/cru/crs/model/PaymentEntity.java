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

@Entity
@Table(name = "PAYMENTS")
public class PaymentEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @Type(type="pg-uuid")
    private UUID id;

    @Column(name = "REGISTRATION_ID", insertable = false, updatable = false)
    @Type(type="pg-uuid")
    private UUID registrationId;

    @Column(name = "AUTHNET_TRANSACTION_ID")
    private Long authnetTransactionId;

    @Column(name = "CC_NAME_ON_CARD")
    private String creditCardNameOnCard;

    @Column(name = "CC_EXPIRATION_MONTH")
    private String creditCardExpirationMonth;

    @Column(name = "CC_EXPIRATION_YEAR")
    private String creditCardExpirationYear;

    @Column(name = "CC_LAST_FOUR_DIGITS")
    private String creditCardLastFourDigits;

    @Column(name = "AMOUNT")
    BigDecimal amount;

    @Column(name = "TRANSACTION_TIMESTAMP")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
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