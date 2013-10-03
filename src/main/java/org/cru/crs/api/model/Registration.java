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

    private Set<Answer> answers = new HashSet<Answer>();
    private List<Payment> payments = new ArrayList<Payment>();
    
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
		webRegistration.conferenceId = jpaRegistration.getConference().getId();
        webRegistration.answers = Answer.fromJpa(jpaRegistration.getAnswers());
        webRegistration.completed = jpaRegistration.getCompleted();

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
        jpaRegistration.setConference(conferenceEntity);
        jpaRegistration.setCompleted(completed);

        jpaRegistration.setAnswers(new HashSet<AnswerEntity>());
        for (Answer answer : getAnswers())
            jpaRegistration.getAnswers().add(answer.toJpaAnswerEntity());

		return jpaRegistration;
	}

	public void addAllPayments(List<PaymentEntity> fetchPaymentsForRegistration)
	{
		for(PaymentEntity jpaPayment : fetchPaymentsForRegistration)
		{
			payments.add(Payment.fromJpa(jpaPayment));
		}
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

	public List<Payment> getPayments()
	{
		return payments;
	}

	public void setPayments(List<Payment> payments)
	{
		this.payments = payments;
	}

}
