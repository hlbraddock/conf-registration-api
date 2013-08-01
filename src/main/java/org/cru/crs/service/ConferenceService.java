package org.cru.crs.service;

import org.cru.crs.model.ConferenceEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

public class ConferenceService
{
	EntityManager em;

    @Inject
	public ConferenceService(EntityManager em)
	{
		this.em = em;
	}

	public List<ConferenceEntity> fetchAllConferences(UUID crsAppUserId)
	{
		return em.createQuery("SELECT conf FROM ConferenceEntity conf " +
								"WHERE conf.contactUser = :crsAppUserId", ConferenceEntity.class)
							.setParameter("crsAppUserId", crsAppUserId)
				 			.getResultList();
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
        return em.find(ConferenceEntity.class, id);
    }
	
	public void createNewConference(ConferenceEntity newConference)
	{
		em.persist(newConference);
	}
	
	public void updateConference(ConferenceEntity conferenceToUpdate)
	{
		em.merge(conferenceToUpdate);
	}

}