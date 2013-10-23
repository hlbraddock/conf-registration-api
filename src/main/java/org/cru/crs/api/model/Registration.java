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
	public static Registration fromJpa(RegistrationEntity jpaRegistration)
	{
		Registration webRegistration = new Registration();
		
		webRegistration.id = jpaRegistration.getId();
        webRegistration.userId = jpaRegistration.getUserId();
//		webRegistration.conferenceId = jpaRegistration.getConference().getId();
//        webRegistration.answers = Answer.fromJpa(jpaRegistration.getAnswers());
        webRegistration.completed = jpaRegistration.getCompleted();
        
//        for(PaymentEntity jpaPayment : jpaRegistration.getPayments())
//        {
//        	if(jpaPayment.getTransactionDatetime() == null || jpaPayment.getAuthnetTransactionId() == null)
//        	{
//        		webRegistration.currentPayment = Payment.fromJpa(jpaPayment);
//        	}
//        	else
//        	{
//        		webRegistration.pastPayments.add(Payment.fromJpa(jpaPayment));
//        	}
//        }
        
        return webRegistration;
	}
	
	public static Set<Registration> fromJpa(Set<RegistrationEntity> jpaRegistrations)
	{
		Set<Registration> registrations = new HashSet<Registration>();
		
		for(RegistrationEntity jpaRegistration : jpaRegistrations)
		{
			registrations.add(fromJpa(jpaRegistration));
		}
		
		return registrations;
	}
	
	public RegistrationEntity toJpaRegistrationEntity(ConferenceEntity conferenceEntity)
	{
		RegistrationEntity jpaRegistration = new RegistrationEntity();
		
		jpaRegistration.setId(id);
		jpaRegistration.setUserId(userId);
//        jpaRegistration.setConference(conferenceEntity);
        jpaRegistration.setCompleted(completed);

//        jpaRegistration.setAnswers(new HashSet<AnswerEntity>());
//        for (Answer answer : getAnswers())
//        {
//            jpaRegistration.getAnswers().add(answer.toJpaAnswerEntity());
//        }
        
//        for(Payment payment : pastPayments)
//        {
//        	jpaRegistration.getPayments().add(payment.toJpaPaymentEntity());
//        }
        
//        if(currentPayment != null) jpaRegistration.getPayments().add(currentPayment.toJpaPaymentEntity());
        
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
