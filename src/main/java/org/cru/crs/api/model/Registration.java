package org.cru.crs.api.model;

import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.RegistrationEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Registration implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;

    private Set<AnswerEntity> answers;

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
        webRegistration.answers = new HashSet<AnswerEntity>(jpaRegistration.getAnswers());
		return webRegistration;
	}
	
	public static List<Registration> fromJpa(List<RegistrationEntity> jpaRegistrations)
	{
		List<Registration> registrations = new ArrayList<Registration>();
		
		for(RegistrationEntity jpaRegistration : jpaRegistrations)
		{
			registrations.add(fromJpa(jpaRegistration));
		}
		
		return registrations;
	}
	
	public RegistrationEntity toJpaRegistrationEntity()
	{
		RegistrationEntity jpaRegistration = new RegistrationEntity();
		
		jpaRegistration.setId(id);
        jpaRegistration.setUserId(userId);
        jpaRegistration.setAnswers(new HashSet<AnswerEntity>(answers));

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

    public Set<AnswerEntity> getAnswers()
    {
        return answers;
    }

    public void setAnswers(Set<AnswerEntity> answers)
    {
        this.answers = answers;
    }
}
