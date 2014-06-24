package org.cru.crs.service;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.queries.PageQueries;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class PageService
{
	org.sql2o.Connection sqlConnection;
	
	BlockService blockService;
	
	PageQueries pageQueries;
	
	/*Weld requires a default no args constructor to proxy this object*/
	public PageService(){ }
	
	@Inject
	public PageService(Connection sqlConnection, BlockService blockService)
	{
		this.sqlConnection = sqlConnection;
		
		this.blockService = blockService;
		
		this.pageQueries = new PageQueries();
	}
	
	public PageEntity getPageById(UUID id)
	{
		return sqlConnection.createQuery(pageQueries.selectById(), false)
						.addParameter("id", id)
						.setAutoDeriveColumnNames(true)
						.executeAndFetchFirst(PageEntity.class);	
	}

	public List<PageEntity> fetchPagesForConference(UUID conferenceId)
	{
		return sqlConnection.createQuery(pageQueries.selectAllForConference(), false)
						.addParameter("conferenceId", conferenceId)
						.setAutoDeriveColumnNames(true)
						.executeAndFetch(PageEntity.class);
	}
	
	public void savePage(PageEntity pageToSave)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sqlConnection.createQuery(pageQueries.insert(),false)
				.addParameter("id", pageToSave.getId())
				.addParameter("conferenceId", pageToSave.getConferenceId())
				.addParameter("position", pageToSave.getPosition())
				.addParameter("title", pageToSave.getTitle())
				.executeUpdate();
	}
	
	public void updatePage(PageEntity pageToUpdate)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sqlConnection.createQuery(pageQueries.update(),false)
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

		sqlConnection.createQuery(pageQueries.delete(),false)
			.addParameter("id", pageId)
			.executeUpdate();
	}
}