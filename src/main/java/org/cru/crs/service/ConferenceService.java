package org.cru.crs.service;

import org.cru.crs.model.ConferenceEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

public class ConferenceService
{
	EntityManager em;
	
	public ConferenceService(EntityManager em)
	{
		this.em = em;
	}

	public List<ConferenceEntity> fetchAllConferences()
	{
		return em.createQuery("SELECT conf FROM ConferenceEntity conf", ConferenceEntity.class)
				 			.getResultList();
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
        return em.createQuery("SELECT conf FROM ConferenceEntity conf WHERE conf.id = :id", ConferenceEntity.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}