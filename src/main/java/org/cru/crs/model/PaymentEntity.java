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

    private String authnetTransactionId;
    private String ccNameOnCard;
    private String ccExpirationMonth;
    private String ccExpirationYear;
    private String ccLastFourDigits;

    private BigDecimal amount;
    private DateTime transactionTimestamp;
    private PaymentType paymentType;
    private UUID updatedByUserId;
    private UUID refundedPaymentId;

    private String checkNumber;
    private String description;
    private String transferSource;

    public boolean isValidCreditCardPayment()
    {
        return (PaymentType.CREDIT_CARD.equals(paymentType) || PaymentType.CREDIT_CARD_REFUND.equals(paymentType)) &&
                getAuthnetTransactionId() != null;
    }

    public boolean isFailedCreditCardPayment()
    {
        return (PaymentType.CREDIT_CARD.equals(paymentType) || PaymentType.CREDIT_CARD_REFUND.equals(paymentType)) &&
                getAuthnetTransactionId() == null;
    }

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

	public String getAuthnetTransactionId()
	{
		return authnetTransactionId;
	}

	public void setAuthnetTransactionId(String authnetTransactionId)
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

	public UUID getUpdatedByUserId()
	{
		return updatedByUserId;
	}

	public void setUpdatedByUserId(UUID updatedByUserId)
	{
		this.updatedByUserId = updatedByUserId;
	}

	public UUID getRefundedPaymentId()
	{
		return refundedPaymentId;
	}

	public void setRefundedPaymentId(UUID refundedPaymentId)
	{
		this.refundedPaymentId = refundedPaymentId;
	}

    public String getCheckNumber()
    {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTransferSource()
    {
        return transferSource;
    }

    public void setTransferSource(String transferSource)
    {
        this.transferSource = transferSource;
    }
}