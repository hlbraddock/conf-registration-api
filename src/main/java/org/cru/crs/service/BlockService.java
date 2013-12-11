package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.queries.BlockQueries;
import org.sql2o.Connection;

@RequestScoped
public class BlockService
{
	org.sql2o.Connection sqlConnection;

	AnswerService answerService;

	BlockQueries blockQueries;
	
	/*required for Weld*/
	public BlockService(){ }
	
    @Inject
	public BlockService(Connection sqlConnection, AnswerService answerService)
	{
    	this.sqlConnection = sqlConnection;

    	this.answerService = answerService;
		
		this.blockQueries = new BlockQueries();
	}
	
	public BlockEntity fetchBlockBy(UUID blockId)
	{
		return sqlConnection.createQuery(blockQueries.selectById(), false)
								.addParameter("id", blockId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(BlockEntity.class);
	}
	
	public List<BlockEntity> fetchBlocksForPage(UUID pageId)
	{
		return sqlConnection.createQuery(blockQueries.selectAllForPage(), false)
								.addParameter("pageId", pageId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(BlockEntity.class);
	}
	
	public void saveBlock(BlockEntity blockToSave)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sqlConnection.createQuery(blockQueries.insert())
						.addParameter("id", blockToSave.getId())
						.addParameter("pageId", blockToSave.getPageId())
						.addParameter("position", blockToSave.getPosition())
						.addParameter("blockType", blockToSave.getBlockType())
						.addParameter("adminOnly", blockToSave.isAdminOnly())
						.addParameter("required", blockToSave.isRequired())
						.addParameter("title", blockToSave.getTitle())
						.addParameter("content", blockToSave.getContent())
						.executeUpdate();
	}
	
	public void updateBlock(BlockEntity blockToUpdate)
	{		
		/*content and conferenceCostsBlocksId omitted for now*/
		sqlConnection.createQuery(blockQueries.update())
						.addParameter("id", blockToUpdate.getId())
						.addParameter("pageId", blockToUpdate.getPageId())
						.addParameter("position", blockToUpdate.getPosition())
						.addParameter("blockType", blockToUpdate.getBlockType())
						.addParameter("adminOnly", blockToUpdate.isAdminOnly())
						.addParameter("required", blockToUpdate.isRequired())
						.addParameter("title", blockToUpdate.getTitle())
						.addParameter("content", blockToUpdate.getContent())
						.executeUpdate();
	}

	/**
	 * Deletes the block specified by @param blockId along with any answers associated with it.
	 * @param owningConference
	 * @param blockId
	 */
	public void deleteBlock(UUID blockId)
	{
		answerService.deleteAnswersByBlockId(blockId);

		sqlConnection.createQuery(blockQueries.delete())
						.addParameter("id", blockId)
						.executeUpdate();
	}
}
