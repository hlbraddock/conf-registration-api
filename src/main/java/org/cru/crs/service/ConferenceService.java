package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;

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
	
	public void createNewConference(ConferenceEntity newConference, UUID crsAppUserId) throws UnauthorizedException
	{
		if(crsAppUserId == null)
		{
			throw new UnauthorizedException();
		}
		
		newConference.setContactUser(crsAppUserId);
		
		em.persist(newConference);
	}
	
	public void updateConference(ConferenceEntity conferenceToUpdate, UUID crsAppUserId) throws UnauthorizedException
	{
		if(crsAppUserId == null || !crsAppUserId.equals(conferenceToUpdate.getContactUser()))
		{
			throw new UnauthorizedException();
		}
		
		em.merge(conferenceToUpdate);
	}
	
	public void addPageToConference(ConferenceEntity conferenceToAddPageTo, PageEntity pageToAdd, UUID crsAppUserId) throws UnauthorizedException
	{
		/*if there is no user ID, or the conference belongs to a different user, the return a 401 - Unauthorized*/
		if(crsAppUserId == null || !crsAppUserId.equals(conferenceToAddPageTo.getContactUser()))
		{
			throw new UnauthorizedException();
		}
		
		/*create a page id if the client didn't*/
		if(pageToAdd.getId() == null) pageToAdd.setId(UUID.randomUUID());
		
		conferenceToAddPageTo.getPages().add(pageToAdd.setConferenceId(conferenceToAddPageTo.getId()));
	}

}