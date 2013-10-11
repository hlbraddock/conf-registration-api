package org.cru.crs.model;


import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "REGISTRATIONS")
public class RegistrationEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type = "pg-uuid")
	private UUID id;

    // NO! cascade = CascadeType.ALL - unless you want to remove the parent (conference)
    @ManyToOne(fetch = FetchType.LAZY)
    private ConferenceEntity conference;

    @Column(name = "USER_ID")
	@Type(type = "pg-uuid")
	private UUID userId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name = "REGISTRATION_ID", nullable = false)
	private Set<AnswerEntity> answers;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval=false)
    @JoinColumn(name = "REGISTRATION_ID")
    private Set<PaymentEntity> payments; 
    
    @Column(name = "COMPLETED")
    private Boolean completed;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

    public ConferenceEntity getConference()
    {
        return conference;
    }

    public void setConference(ConferenceEntity conference)
    {
        this.conference = conference;
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

    public Boolean getCompleted()
    {
        return completed;
    }

    public void setCompleted(Boolean completed)
    {
        this.completed = completed;
    }

	public Set<PaymentEntity> getPayments()
	{
		return payments;
	}

	public void setPayments(Set<PaymentEntity> payments)
	{
		this.payments = payments;
	}
}
