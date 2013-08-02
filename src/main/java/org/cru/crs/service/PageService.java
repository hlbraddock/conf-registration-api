package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;

public class PageService
{
	EntityManager em;
	ConferenceService conferenceService;

    @Inject
	public PageService(EntityManager em, ConferenceService conferenceService)
	{
		this.em = em;
		this.conferenceService = conferenceService;
	}
	
	public PageEntity fetchPageBy(UUID id)
	{
		return em.find(PageEntity.class, id);
	}
	
	public void updatePage(PageEntity pageToUpdate, UUID crsAppUserId) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisPagesConference(pageToUpdate,crsAppUserId);
		
		em.merge(pageToUpdate);
	}
	
	public void deletePage(PageEntity pageToDelete, UUID crsAppUserId) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisPagesConference(pageToDelete, crsAppUserId);
		
		em.remove(em.find(PageEntity.class, pageToDelete.getId()));
	}

	private void verifyUserIdHasAccessToModifyThisPagesConference(PageEntity pageToDelete, UUID crsAppUserId)
			throws UnauthorizedException
	{
		ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(pageToDelete.getConferenceId());
		
		/*This could NPE if the conference is null, I considered adding an extra check here, but
		 * the end result would only be that a different exception be thrown.  Either way it
		 * will end up as a 500 to the client.*/
		if(crsAppUserId == null || !crsAppUserId.equals(conferencePageBelongsTo.getContactUser()))
		{
			throw new UnauthorizedException();
		}
	}
}