package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;

public class BlockService
{
	EntityManager em;
	PageService pageService;
	ConferenceService conferenceService;

    @Inject
	public BlockService(EntityManager em, ConferenceService conferenceService, PageService pageService)
	{
		this.em = em;
		this.conferenceService = conferenceService;
		this.pageService = pageService;
	}
	
	public BlockEntity fetchBlockBy(UUID blockId)
	{
		return em.find(BlockEntity.class, blockId);
	}
	
	public void updateBlock(BlockEntity blockToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisBlocksConference(blockToUpdate,crsLoggedInUser);
		
		em.merge(blockToUpdate);
	}

	public void deleteBlock(UUID blockId, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		BlockEntity blockToDelete = em.find(BlockEntity.class, blockId);
		
		verifyUserIdHasAccessToModifyThisBlocksConference(blockToDelete,crsLoggedInUser);
		
		em.remove(blockToDelete);
	}
	
	private void verifyUserIdHasAccessToModifyThisBlocksConference(BlockEntity blockToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		PageEntity pageBlockBelongsTo = pageService.fetchPageBy(blockToUpdate.getPageId());
		ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(pageBlockBelongsTo.getConferenceId());
		
		/*This could NPE if the conference is null, I considered adding an extra check here, but
		 * the end result would only be that a different exception be thrown.  Either way it
		 * will end up as a 500 to the client.*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferencePageBelongsTo.getContactUser()))
		{
			throw new UnauthorizedException();
		}
		
	}
}
