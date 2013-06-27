package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.ConferenceEntity_;

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
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		CriteriaQuery<ConferenceEntity> query = builder.createQuery(ConferenceEntity.class);
		
		Root<ConferenceEntity> root = query.from(ConferenceEntity.class);
		
		query.where(builder.equal(root.get(ConferenceEntity_.id), id));
		
		return em.createQuery(query).getSingleResult();
	}
}