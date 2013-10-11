package org.cru.crs.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.PaymentEntity;
import org.joda.time.DateTime;


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
    private boolean readyToProcess;

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

        jpaPayment.setAuthnetTransactionId(authnetTransactionId);
        jpaPayment.setTransactionDatetime(transactionDatetime);
        return jpaPayment;
    }
	
    public static Payment fromJpa(PaymentEntity jpaPayment)
	{
		if(jpaPayment == null) return null;
    	Payment payment = new Payment();
		
		payment.id = jpaPayment.getId();
		payment.registrationId = jpaPayment.getRegistrationId();
		payment.amount = jpaPayment.getAmount();

		payment.creditCardExpirationMonth = jpaPayment.getCreditCardExpirationMonth();
		payment.creditCardExpirationYear = jpaPayment.getCreditCardExpirationYear();
		payment.creditCardNameOnCard = jpaPayment.getCreditCardNameOnCard();
		payment.authnetTransactionId = jpaPayment.getAuthnetTransactionId();
		payment.creditCardLastFourDigits = jpaPayment.getCreditCardLastFourDigits();
		payment.transactionDatetime = jpaPayment.getTransactionDatetime();
		
		if(jpaPayment.getCreditCardLastFourDigits() != null) payment.creditCardNumber = "****" + jpaPayment.getCreditCardLastFourDigits();
				
		return payment;
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
    
}
