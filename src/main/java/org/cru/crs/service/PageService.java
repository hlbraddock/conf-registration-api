package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.queries.PageQueries;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.sql2o.Sql2o;

public class PageService
{
	Sql2o sql;
	
	BlockService blockService;
	
	PageQueries pageQueries;
	
	@Inject
	public PageService(Sql2o sql, BlockService blockService)
	{
		this.sql = sql;
		
		this.blockService = blockService;
		
		this.pageQueries = new PageQueries();
	}
	
	public PageEntity fetchPageBy(UUID id)
	{
		return sql.createQuery(pageQueries.selectById(), false)
						.addParameter("id", id)
						.setAutoDeriveColumnNames(true)
						.executeAndFetchFirst(PageEntity.class);	
	}

	public List<PageEntity> fetchPagesForConference(UUID conferenceId)
	{
		return sql.createQuery(pageQueries.selectAllForConference(), false)
						.addParameter("conferenceId", conferenceId)
						.setAutoDeriveColumnNames(true)
						.executeAndFetch(PageEntity.class);
	}
	
	public void savePage(PageEntity pageToSave)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(pageQueries.insert(),false)
				.addParameter("id", pageToSave.getId())
				.addParameter("conferenceId", pageToSave.getConferenceId())
				.addParameter("position", pageToSave.getPosition())
				.addParameter("title", pageToSave.getTitle())
				.executeUpdate();
	}
	
	public void updatePage(PageEntity pageToUpdate)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(pageQueries.update(),false)
				.addParameter("id", pageToUpdate.getId())
				.addParameter("conferenceId", pageToUpdate.getConferenceId())
				.addParameter("position", pageToUpdate.getPosition())
				.addParameter("title", pageToUpdate.getTitle())
				.executeUpdate();	
	}

	/**
	 * Deletes the page specified by @param pageId along with any blocks associated with it.
	 * @param blockId
	 */
	public void deletePage(UUID pageId)
	{
		for(BlockEntity blockToDelete : blockService.fetchBlocksForPage(pageId))
		{
			blockService.deleteBlock(blockToDelete.getId());
		}

		sql.createQuery(pageQueries.delete(),false)
			.addParameter("id", pageId)
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

}