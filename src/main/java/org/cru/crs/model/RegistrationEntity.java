package org.cru.crs.model;


import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "CRU_CRS_REGISTRATIONS")
public class RegistrationEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type = "pg-uuid")
	private UUID id;

    // NO! cascade = CascadeType.ALL - unless you want to remove the parent (conference)
    @ManyToOne(fetch = FetchType.EAGER)
    private ConferenceEntity conference;

    @Column(name = "USER_ID", insertable = false, updatable = false)
	@Type(type = "pg-uuid")
	private UUID userId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "REGISTRATION_ID", nullable = false)
	private Set<AnswerEntity> answers;

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
}
