package org.cru.crs.api.model;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;



public class Registration implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

    private UUID id;
	private UUID userId;
	private UUID conferenceId;
	private BigDecimal totalDue;
    private boolean completed;
    private DateTime completedTimestamp;
    
    private Payment currentPayment;
    
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
	
	public static Registration fromDb(RegistrationEntity dbRegistration, List<AnswerEntity> dbAnswers, List<PaymentEntity> dbPastPayments, PaymentEntity dbCurrentPayment)
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
				webRegistration.pastPayments.add(Payment.fromJpa(dbPastPayment));
			}
		}
		webRegistration.currentPayment = Payment.fromJpa(dbCurrentPayment);
		
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
		/*total due is omitted b/c that value would never come from the client*/
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

	public void setPastPayments(List<Payment> pastPayments)
	{
		this.pastPayments = pastPayments;
	}

	public Payment getCurrentPayment()
	{
		return currentPayment;
	}

	public void setCurrentPayment(Payment currentPayment)
	{
		this.currentPayment = currentPayment;
	}

	public BigDecimal getTotalDue()
	{
		return totalDue;
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
