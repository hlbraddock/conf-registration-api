package org.cru.crs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.common.collect.Lists;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.utils.CollectionUtils;

public class PageService
{
	EntityManager em;
	ConferenceService conferenceService;
	AnswerService answerService;

    @Inject
	public PageService(EntityManager em, ConferenceService conferenceService, AnswerService answerService)
	{
		this.em = em;
		this.conferenceService = conferenceService;
		this.answerService = answerService;
	}
	
	public PageEntity fetchPageBy(UUID id)
	{
		return em.find(PageEntity.class, id);
	}

	public void updatePage(PageEntity pageToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		updatePage(pageToUpdate, crsLoggedInUser, true);
	}

	public void updatePage(PageEntity pageToUpdate, CrsApplicationUser crsLoggedInUser, boolean withAnswerDeletion) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisPagesConference(pageToUpdate, crsLoggedInUser.getId());

		if(withAnswerDeletion)
			deleteAnswersOnPageUpdate(pageToUpdate);

		em.merge(pageToUpdate);
	}

	public void deleteAnswersOnPagesUpdate(List<PageEntity> currentPages, List<PageEntity> updatePages)
	{
		Set<BlockEntity> blocksInUpdatedPages = new HashSet<BlockEntity>();
		for(PageEntity page : updatePages)
		{
			blocksInUpdatedPages.addAll(page.getBlocks());
		}

		Set<BlockEntity> blocksInCurrentPages = new HashSet<BlockEntity>();
		for(PageEntity page : currentPages)
		{
			blocksInCurrentPages.addAll(page.getBlocks());
		}

		// get all current page blocks not found in update pages
		List<BlockEntity> blocksToDelete =
				CollectionUtils.firstNotFoundInSecond(Lists.newArrayList(blocksInCurrentPages), Lists.newArrayList(blocksInUpdatedPages));

		// delete each (to be) deleted blocks associated answers (since we no longer rely on jpa to automatically do so)
		for(BlockEntity blockEntity : blocksToDelete)
		{
			answerService.deleteAnswersByBlockId(blockEntity.getId());
		}
	}

	private void deleteAnswersOnPageUpdate(PageEntity updatePage) throws UnauthorizedException
	{
		List<BlockEntity> currentBlocks = fetchPageBy(updatePage.getId()).getBlocks();

		// get all current page blocks not found in update page
		List<BlockEntity> deleteBlocks = CollectionUtils.firstNotFoundInSecond(currentBlocks, updatePage.getBlocks());

		// delete each blocks associated answers (since we no longer rely on jpa to automatically do so)
		for(BlockEntity blockEntity : deleteBlocks)
			answerService.deleteAnswersByBlockId(blockEntity.getId());
	}

	public void deletePage(UUID pageId, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		PageEntity pageToDelete = em.find(PageEntity.class, pageId);

		verifyUserIdHasAccessToModifyThisPagesConference(pageToDelete, crsLoggedInUser.getId());

		// delete each blocks associated answers (since we no longer rely on jpa to automatically do so)
		for(BlockEntity blockEntity : pageToDelete.getBlocks())
			answerService.deleteAnswersByBlockId(blockEntity.getId());

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