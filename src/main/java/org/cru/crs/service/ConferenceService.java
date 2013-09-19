package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
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

	public List<ConferenceEntity> fetchAllConferences(CrsApplicationUser crsLoggedInUser)
	{
		return em.createQuery("SELECT conf FROM ConferenceEntity conf " +
								"WHERE conf.contactUser = :crsAppUserId", ConferenceEntity.class)
							.setParameter("crsAppUserId", crsLoggedInUser.getId())
				 			.getResultList();
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
        return em.find(ConferenceEntity.class, id);
    }
	
	public void createNewConference(ConferenceEntity newConference, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		if(crsLoggedInUser == null || crsLoggedInUser.isCrsAuthenticatedOnly())
		{
			throw new UnauthorizedException();
		}
		
		newConference.setContactUser(crsLoggedInUser.getId());
		
		em.persist(newConference);
	}
	
	public void updateConference(ConferenceEntity conferenceToUpdate,  CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferenceToUpdate.getContactUser()))
		{
			throw new UnauthorizedException();
		}

        /*
         * So that blocks don't get deleting when moving them to a preceding page, update pages
         * one by one and flush to the database between moving them.  See Github issue 39 and PR 42 for context
         */

        //can't inject a PageService, because PageService injects this class.
        PageService pageService = new PageService(em,this, new AnswerService(em));
        for(PageEntity page : conferenceToUpdate.getPages())
        {
            pageService.updatePage(page, crsLoggedInUser, false);
            em.flush();
        }

		em.merge(conferenceToUpdate);
	}
	
	public void addPageToConference(ConferenceEntity conferenceToAddPageTo, PageEntity pageToAdd,  CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		/*if there is no user ID, or the conference belongs to a different user, the return a 401 - Unauthorized*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferenceToAddPageTo.getContactUser()))
		{
			throw new UnauthorizedException();
		}
		
		/*create a page id if the client didn't*/
		if(pageToAdd.getId() == null) pageToAdd.setId(UUID.randomUUID());
		
		conferenceToAddPageTo.getPages().add(pageToAdd.setConferenceId(conferenceToAddPageTo.getId()));
	}

}