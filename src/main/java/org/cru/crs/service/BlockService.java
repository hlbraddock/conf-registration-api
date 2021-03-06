package org.cru.crs.service;


import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.queries.BlockQueries;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class BlockService
{
	org.sql2o.Connection sqlConnection;

	AnswerService answerService;

	BlockQueries blockQueries;
	
	/*Weld requires a default no args constructor to proxy this object*/
	public BlockService(){ }
	
    @Inject
	public BlockService(Connection sqlConnection, AnswerService answerService)
	{
    	this.sqlConnection = sqlConnection;

    	this.answerService = answerService;
		
		this.blockQueries = new BlockQueries();
	}
	
	public BlockEntity getBlockById(UUID blockId)
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
		/*type cast Enum to Object so sql2o correctly writes postgres enum type, o/w sql2o will convert to String*/
						.addParameter("profile_type", (Object) blockToSave.getProfileType())
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
		/*type cast Enum to Object so sql2o correctly writes postgres enum type, o/w sql2o will convert to String*/
						.addParameter("profile_type", (Object) blockToUpdate.getProfileType())
						.executeUpdate();
	}

	/**
	 * Deletes the block specified by @param blockId along with any answers associated with it.
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
