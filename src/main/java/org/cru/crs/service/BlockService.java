package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.queries.BlockQueries;
import org.sql2o.Sql2o;

public class BlockService
{
	Sql2o sql;

	AnswerService answerService;

	BlockQueries blockQueries;
	
    @Inject
	public BlockService(Sql2o sql, AnswerService answerService)
	{
    	this.sql = sql;

    	this.answerService = answerService;
		
		this.blockQueries = new BlockQueries();
	}
	
	public BlockEntity fetchBlockBy(UUID blockId)
	{
		return sql.createQuery(blockQueries.selectById(), false)
						.addParameter("id", blockId)
						.setAutoDeriveColumnNames(true)
						.executeAndFetchFirst(BlockEntity.class);
	}
	
	public List<BlockEntity> fetchBlocksForPage(UUID pageId)
	{
		return sql.createQuery(blockQueries.selectAllForPage(), false)
						.addParameter("pageId", pageId)
						.setAutoDeriveColumnNames(true)
						.executeAndFetch(BlockEntity.class);
	}
	
	public void saveBlock(BlockEntity blockToSave)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(blockQueries.insert())
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
		sql.createQuery(blockQueries.update())
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

		sql.createQuery(blockQueries.delete())
				.addParameter("id", blockId)
				.executeUpdate();
	}
}
