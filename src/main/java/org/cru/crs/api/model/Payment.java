package org.cru.crs.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Payment implements Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
    private UUID registrationId;
    private BigDecimal amount;
    private DateTime transactionDatetime;
    private PaymentType paymentType;
    private UUID refundedPaymentId;
    private boolean readyToProcess;

    private CreditCardPayment creditCard;
    private TransferPayment transfer;
    private ScholarshipPayment scholarship;
    private CheckPayment check;

    public PaymentEntity toDbPaymentEntity()
    {
        PaymentEntity dbPayment = new PaymentEntity();

        dbPayment.setId(id);
        dbPayment.setRegistrationId(registrationId);
        dbPayment.setAmount(amount);
        dbPayment.setTransactionTimestamp(transactionDatetime);
        dbPayment.setRefundedPaymentId(refundedPaymentId);
        dbPayment.setPaymentType(paymentType);

        if((PaymentType.CREDIT_CARD.equals(paymentType) ||
                PaymentType.CREDIT_CARD_REFUND.equals(paymentType)) && creditCard != null)
        {
            dbPayment.setCcExpirationMonth(creditCard.expirationMonth);
            dbPayment.setCcExpirationYear(creditCard.expirationYear);
            dbPayment.setCcNameOnCard(creditCard.nameOnCard);
        
            if(creditCard.number != null)
            {
                dbPayment.setCcLastFourDigits(creditCard.number.substring(Math.max(0, creditCard.number.length() - 4)));
            }

            dbPayment.setAuthnetTransactionId(creditCard.authnetTransactionId);
        }
        else if(PaymentType.CHECK.equals(paymentType) && check != null)
        {
            dbPayment.setCheckNumber(check.getCheckNumber());
        }
        else if(PaymentType.TRANSFER.equals(paymentType) && transfer != null)
        {
            dbPayment.setTransferSource(transfer.source);
            dbPayment.setDescription(transfer.description);
        }
        else if(PaymentType.SCHOLARSHIP.equals(paymentType) && scholarship != null)
        {
            dbPayment.setDescription(scholarship.description);
        }


        
        return dbPayment;
    }
	
    public static Payment fromDb(PaymentEntity dbPayment)
	{
		if(dbPayment == null) return null;
    	Payment payment = new Payment();
		
		payment.id = dbPayment.getId();
		payment.registrationId = dbPayment.getRegistrationId();
		payment.amount = dbPayment.getAmount();
        payment.transactionDatetime = dbPayment.getTransactionTimestamp();
        payment.refundedPaymentId = dbPayment.getRefundedPaymentId();

        payment.paymentType = dbPayment.getPaymentType();

        if(PaymentType.CREDIT_CARD.equals(dbPayment.getPaymentType()) || PaymentType.CREDIT_CARD_REFUND.equals(dbPayment.getPaymentType()))
        {
            payment.creditCard = new CreditCardPayment(dbPayment);
        }
        else if(PaymentType.CHECK.equals(dbPayment.getPaymentType()))
        {
            payment.check = new CheckPayment(dbPayment);
        }
        else if(PaymentType.SCHOLARSHIP.equals(dbPayment.getPaymentType()))
        {
            payment.scholarship = new ScholarshipPayment(dbPayment);
        }
        else if(PaymentType.TRANSFER.equals(dbPayment.getPaymentType()))
        {
            payment.transfer = new TransferPayment(dbPayment);
        }

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

    public CreditCardPayment getCreditCard()
    {
        return creditCard;
    }

    public void setCreditCard(CreditCardPayment creditCard)
    {
        this.creditCard = creditCard;
    }

    public TransferPayment getTransfer()
    {
        return transfer;
    }

    public void setTransfer(TransferPayment transfer)
    {
        this.transfer = transfer;
    }

    public ScholarshipPayment getScholarship()
    {
        return scholarship;
    }

    public void setScholarship(ScholarshipPayment scholarship)
    {
        this.scholarship = scholarship;
    }

    public CheckPayment getCheck()
    {
        return check;
    }

    public void setCheck(CheckPayment check)
    {
        this.check = check;
    }

    public static class CreditCardPayment
    {
        private String number;
        private String nameOnCard;
        private String expirationMonth;
        private String expirationYear;
        private String lastFourDigits;
        private String cvvNumber;
        private String authnetTransactionId;

        public CreditCardPayment(){}
        public CreditCardPayment(PaymentEntity databasePayment)
        {
            lastFourDigits = databasePayment.getCcLastFourDigits();
            expirationMonth = databasePayment.getCcExpirationMonth();
            expirationYear = databasePayment.getCcExpirationYear();
            authnetTransactionId = databasePayment.getAuthnetTransactionId();
            nameOnCard = databasePayment.getCcNameOnCard();

            if(databasePayment.getCcLastFourDigits() != null) number = "****" + databasePayment.getCcLastFourDigits();
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getNameOnCard() {
            return nameOnCard;
        }

        public void setNameOnCard(String nameOnCard) {
            this.nameOnCard = nameOnCard;
        }

        public String getExpirationMonth() {
            return expirationMonth;
        }

        public void setExpirationMonth(String expirationMonth) {
            this.expirationMonth = expirationMonth;
        }

        public String getExpirationYear() {
            return expirationYear;
        }

        public void setExpirationYear(String expirationYear) {
            this.expirationYear = expirationYear;
        }

        public String getLastFourDigits() {
            return lastFourDigits;
        }

        public void setLastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
        }

        public String getCvvNumber() {
            return cvvNumber;
        }

        public void setCvvNumber(String cvvNumber) {
            this.cvvNumber = cvvNumber;
        }

		public String getAuthnetTransactionId()
		{
			return authnetTransactionId;
		}

		public void setAuthnetTransactionId(String authnetTransactionId)
		{
			this.authnetTransactionId = authnetTransactionId;
		}
	}

    public static class TransferPayment
    {
        private String source;
        private String description;

        public TransferPayment() {}

        public TransferPayment(PaymentEntity databasePayment)
        {
            this.source = databasePayment.getTransferSource();
            this.description = databasePayment.getDescription();
        }

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }
    }

    public static class ScholarshipPayment
    {
        private String description;

        public ScholarshipPayment() {}

        public ScholarshipPayment(PaymentEntity databasePayment)
        {
            this.description = databasePayment.getDescription();
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }
    }

    public static class CheckPayment
    {
        private String checkNumber;

        public CheckPayment() { }

        public CheckPayment(PaymentEntity databasePayment)
        {
            this.checkNumber = databasePayment.getCheckNumber();
        }

        public String getCheckNumber()
        {
            return checkNumber;
        }

        public void setCheckNumber(String checkNumber)
        {
            this.checkNumber = checkNumber;
        }
    }
}
