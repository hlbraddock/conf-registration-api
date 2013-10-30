package org.cru.crs.api.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;

public class Registration implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

    private UUID id;
	private UUID userId;
	private UUID conferenceId;
    private Boolean completed;
    private Payment currentPayment;
    
    private Set<Answer> answers = new HashSet<Answer>();
    private List<Payment> pastPayments = new ArrayList<Payment>();
    
	/**
	 * Creates a web api friendly registration
	 * 
	 * @param jpaRegistration
	 * @return
	 */
	public static Registration fromDb(RegistrationEntity jpaRegistration)
	{
		Registration webRegistration = new Registration();
		
		webRegistration.id = jpaRegistration.getId();
        webRegistration.userId = jpaRegistration.getUserId();
		webRegistration.conferenceId = jpaRegistration.getConferenceId();
        webRegistration.completed = jpaRegistration.getCompleted();
        
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
		RegistrationEntity jpaRegistration = new RegistrationEntity();
		
		jpaRegistration.setId(id);
		jpaRegistration.setUserId(userId);
        jpaRegistration.setCompleted(completed);
        jpaRegistration.setConferenceId(conferenceId);
        
		return jpaRegistration;
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

    public Boolean getCompleted()
    {
        return completed;
    }

    public void setCompleted(Boolean completed)
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

}
