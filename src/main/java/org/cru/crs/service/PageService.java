package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.cru.crs.model.queries.PageQueries;
import org.sql2o.Sql2o;

public class PageService
{
	Sql2o sql;
	
	BlockService blockService;
	
	PageQueries pageQueries;
	
	@Inject
	public PageService(Sql2o sql, BlockService blockService, PageQueries pageQueries)
	{
		this.sql = sql;
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(PageEntity.class));
		
		this.blockService = blockService;
		
		this.pageQueries = pageQueries;
	}
	
	public PageEntity fetchPageBy(UUID id)
	{
		return sql.createQuery(pageQueries.selectById(), false)
						.addParameter("id", id)
						.executeAndFetchFirst(PageEntity.class);	
	}

	public List<PageEntity> fetchPagesForConference(UUID conferenceId)
	{
		return sql.createQuery(pageQueries.selectAllForConference(), false)
						.addParameter("conferenceId", conferenceId)
						.executeAndFetch(PageEntity.class);
	}
	
	public void savePage(PageEntity pageToSave)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(pageQueries.insert())
				.addParameter("id", pageToSave.getId())
				.addParameter("conferenceId", pageToSave.getConferenceId())
				.addParameter("position", pageToSave.getPosition())
				.addParameter("title", pageToSave.getTitle())
				.executeUpdate();
	}
	
	public void updatePage(ConferenceEntity owningConference, PageEntity pageToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisPagesConference(owningConference, pageToUpdate, crsLoggedInUser.getId());

		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(pageQueries.update())
				.addParameter("id", pageToUpdate.getId())
				.addParameter("conferenceId", pageToUpdate.getConferenceId())
				.addParameter("position", pageToUpdate.getPosition())
				.addParameter("title", pageToUpdate.getTitle())
				.executeUpdate();	
	}

	public void deletePage(ConferenceEntity owningConference, UUID pageId, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisPagesConference(owningConference, fetchPageBy(pageId), crsLoggedInUser.getId());

		for(BlockEntity blockToDelete : blockService.fetchBlocksForPage(pageId))
		{
			blockService.deleteBlock(blockToDelete);
		}

		sql.createQuery(pageQueries.delete())
			.addParameter("pageId", pageId)
			.executeUpdate();
	}

	public void addBlockToPage(ConferenceEntity owningConference, PageEntity pageToAddBlockTo, BlockEntity blockToAdd, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{		
		/*if there is no user ID, or the conference belongs to a different user, the return a 401 - Unauthorized*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(owningConference.getContactPersonId()))
		{
			throw new UnauthorizedException();
		}
		
		/*create a block id if the client didn't*/
		if(blockToAdd.getId() == null) blockToAdd.setId(UUID.randomUUID());
		blockToAdd.setPageId(pageToAddBlockTo.getId());
		blockService.saveBlock(blockToAdd);
	}

	private void verifyUserIdHasAccessToModifyThisPagesConference(ConferenceEntity owningConference, PageEntity pageToDelete, UUID crsAppUserId)
			throws UnauthorizedException
	{		
		/*This could NPE if the conference is null, I considered adding an extra check here, but
		 * the end result would only be that a different exception be thrown.  Either way it
		 * will end up as a 500 to the client.*/
		if(crsAppUserId == null || !crsAppUserId.equals(owningConference.getContactPersonId()))
		{
			throw new UnauthorizedException();
		}
	}
}