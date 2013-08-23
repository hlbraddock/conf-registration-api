package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
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
	
	public void updatePage(PageEntity pageToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisPagesConference(pageToUpdate, crsLoggedInUser.getId());
		
		em.merge(pageToUpdate);
	}
	
	public void deletePage(UUID pageId, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		PageEntity pageToDelete = em.find(PageEntity.class, pageId);
		
		verifyUserIdHasAccessToModifyThisPagesConference(pageToDelete, crsLoggedInUser.getId());
		
		em.remove(pageToDelete);
	}
	
	public void addBlockToPage(PageEntity pageToAddBlockTo, BlockEntity blockToAdd, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(pageToAddBlockTo.getConferenceId());
		
		/*if there is no user ID, or the conference belongs to a different user, the return a 401 - Unauthorized*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferencePageBelongsTo.getContactUser()))
		{
			throw new UnauthorizedException();
		}
		
		/*create a block id if the client didn't*/
		if(blockToAdd.getId() == null) blockToAdd.setId(UUID.randomUUID());
		
		pageToAddBlockTo.getBlocks().add(blockToAdd.setPageId(pageToAddBlockTo.getId()));
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