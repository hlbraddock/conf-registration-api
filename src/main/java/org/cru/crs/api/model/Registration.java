package org.cru.crs.api.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.cru.crs.model.RegistrationEntity;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;



public class Registration implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

    private UUID id;
	private UUID userId;
	private UUID conferenceId;

    private BigDecimal totalDue;

    private boolean completed;
    private DateTime completedTimestamp;

    private Set<Answer> answers = Sets.newHashSet();
    private List<Payment> pastPayments = Lists.newArrayList();

	/**
	 * Creates a web api friendly registration
	 * 
	 * @param dbRegistration
	 * @return
	 */
	public static Registration fromDb(RegistrationEntity dbRegistration)
	{
		if(dbRegistration == null) return null;
		
		Registration webRegistration = new Registration();
		
		webRegistration.id = dbRegistration.getId();
        webRegistration.userId = dbRegistration.getUserId();
		webRegistration.conferenceId = dbRegistration.getConferenceId();
		webRegistration.totalDue = dbRegistration.getTotalDue();
        webRegistration.completed = dbRegistration.getCompleted();
        webRegistration.completedTimestamp = dbRegistration.getCompletedTimestamp();
        
        return webRegistration;
	}
	
	public static Registration fromDb(RegistrationEntity dbRegistration, List<AnswerEntity> dbAnswers, List<PaymentEntity> dbPastPayments)
	{
		Registration webRegistration = fromDb(dbRegistration);
		
		if(webRegistration == null) return null;
		
		if(dbAnswers != null)
		{
			webRegistration.answers = Sets.newHashSet();
			for(AnswerEntity dbAnswer : dbAnswers)
			{
				webRegistration.answers.add(Answer.fromDb(dbAnswer));
			}
		}
		if(dbPastPayments != null)
		{
			webRegistration.pastPayments = Lists.newArrayList();
			for(PaymentEntity dbPastPayment : dbPastPayments)
			{
				webRegistration.pastPayments.add(Payment.fromDb(dbPastPayment));
			}
		}

		return webRegistration;
	}
	
	public static Set<Registration> fromDb(Set<RegistrationEntity> dbRegistrations)
	{
		Set<Registration> registrations = new HashSet<Registration>();
		
		for(RegistrationEntity dbRegistration : dbRegistrations)
		{
			registrations.add(fromDb(dbRegistration));
		}
		
		return registrations;
	}
	
	public RegistrationEntity toDbRegistrationEntity()
	{
		RegistrationEntity dbRegistration = new RegistrationEntity();
		
		dbRegistration.setId(id);
		dbRegistration.setUserId(userId);
		/*total due is omitted b/c that value doesn't typically come from the client.  administrators
		 * can override the total due, but that is handled specifically in UpdateRegistrationProcess*/
		dbRegistration.setCompleted(completed);
		dbRegistration.setConferenceId(conferenceId);
        /*completed timestamp is omitted b/c that value would never come from the client*/
		
		return dbRegistration;
	}
	
	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

    public UUID getUserId()
    {
        return userId;
    }

    public void setUserId(UUID userId)
    {
        this.userId = userId;
    }

    public Set<Answer> getAnswers()
    {
        return answers;
    }

    public void setAnswers(Set<Answer> answers)
    {
        this.answers = answers;
    }

	public UUID getConferenceId()
	{
		return conferenceId;
	}

	public void setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
	}

    public boolean getCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

	public List<Payment> getPastPayments()
	{
		return pastPayments;
	}

	public BigDecimal getTotalDue()
	{
		return totalDue;
	}

    public BigDecimal getTotalPaid()
    {
        BigDecimal bigDecimal = new BigDecimal(0);

        for(Payment payment : getPastPayments())
        {
            if(PaymentType.CREDIT_CARD_REFUND.equals(payment.getPaymentType()) ||
                    PaymentType.REFUND.equals(payment.getPaymentType()))
            {
                bigDecimal = bigDecimal.subtract(payment.getAmount());
            }
            else
            {
                bigDecimal = bigDecimal.add(payment.getAmount());
            }
        }

        return bigDecimal;
    }

    public void setTotalPaid(BigDecimal totalPaid)
    {
        /*do nothing*/
    }
    public BigDecimal getRemainingBalance()
    {
        return getTotalDue() == null ? new BigDecimal(0) : getTotalDue().subtract(getTotalPaid());
    }

    public void setRemainingBalance(BigDecimal remainingBalance)
    {
        /*do nothing*/
    }
    public void setTotalDue(BigDecimal totalDue)
	{
		this.totalDue = totalDue;
	}

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getCompletedTimestamp()
	{
		return completedTimestamp;
	}
	
	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setCompletedTimestamp(DateTime completedTimestamp)
	{
		this.completedTimestamp = completedTimestamp;
	}
}
