package org.cru.crs.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;


public class Payment implements Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
    private UUID registrationId;
    private String creditCardNameOnCard;
    private String creditCardExpirationMonth;
    private String creditCardExpirationYear;
    private String creditCardLastFourDigits;
    private String creditCardCVVNumber;
	private Long authnetTransactionId;
    private String creditCardNumber;
    private BigDecimal amount;
    private DateTime transactionDatetime;
    private PaymentType paymentType;
    private UUID refundedPaymentId;
    private boolean readyToProcess;

    public PaymentEntity toDbPaymentEntity()
    {
        PaymentEntity dbPayment = new PaymentEntity();

        dbPayment.setId(id);
        dbPayment.setRegistrationId(registrationId);
        dbPayment.setAmount(amount);
        dbPayment.setCcExpirationMonth(creditCardExpirationMonth);
        dbPayment.setCcExpirationYear(creditCardExpirationYear);
        dbPayment.setCcNameOnCard(creditCardNameOnCard);
        
        if(creditCardNumber != null)
        {
            dbPayment.setCcLastFourDigits(creditCardNumber.substring(Math.max(0, creditCardNumber.length() - 4)));
        }

        dbPayment.setAuthnetTransactionId(authnetTransactionId);
        dbPayment.setTransactionTimestamp(transactionDatetime);
        dbPayment.setPaymentType(paymentType);
        dbPayment.setRefundedPaymentId(refundedPaymentId);
        
        return dbPayment;
    }
	
    public static Payment fromDb(PaymentEntity dbPayment)
	{
		if(dbPayment == null) return null;
    	Payment payment = new Payment();
		
		payment.id = dbPayment.getId();
		payment.registrationId = dbPayment.getRegistrationId();
		payment.amount = dbPayment.getAmount();

		payment.creditCardExpirationMonth = dbPayment.getCcExpirationMonth();
		payment.creditCardExpirationYear = dbPayment.getCcExpirationYear();
		payment.creditCardNameOnCard = dbPayment.getCcNameOnCard();
		payment.authnetTransactionId = dbPayment.getAuthnetTransactionId();
		payment.creditCardLastFourDigits = dbPayment.getCcLastFourDigits();
		payment.transactionDatetime = dbPayment.getTransactionTimestamp();
		payment.paymentType = dbPayment.getPaymentType();
		payment.refundedPaymentId = dbPayment.getRefundedPaymentId();
		
		if(dbPayment.getCcLastFourDigits() != null) payment.creditCardNumber = "****" + dbPayment.getCcLastFourDigits();
				
		return payment;
	}

    public static List<Payment> fromDb(List<PaymentEntity> dbPayments)
    {
    	List<Payment> apiPayments = Lists.newArrayList();
		
		for(PaymentEntity databasePayment : dbPayments)
		{
			apiPayments.add(Payment.fromDb(databasePayment));
		}
		
		return apiPayments;
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

	public String getCreditCardLastFourDigits()
	{
		return creditCardLastFourDigits;
	}

	public void setCreditCardLastFourDigits(String creditCardLastFourDigits)
	{
		this.creditCardLastFourDigits = creditCardLastFourDigits;
	}

	public String getCreditCardCVVNumber()
	{
		return creditCardCVVNumber;
	}

	public void setCreditCardCVVNumber(String creditCardCVVNumber)
	{
		this.creditCardCVVNumber = creditCardCVVNumber;
	}

	public Long getAuthnetTransactionId()
	{
		return authnetTransactionId;
	}

	public void setAuthnetTransactionId(Long authnetTransactionId)
	{
		this.authnetTransactionId = authnetTransactionId;
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

	public boolean isReadyToProcess()
	{
		return readyToProcess;
	}

	public void setReadyToProcess(boolean readyToProcess)
	{
		this.readyToProcess = readyToProcess;
	}

	public PaymentType getPaymentType()
	{
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}

	public UUID getRefundedPaymentId()
	{
		return refundedPaymentId;
	}

	public void setRefundedPaymentId(UUID refundedPaymentId)
	{
		this.refundedPaymentId = refundedPaymentId;
	}
    
}
