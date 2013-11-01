package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
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
				.executeUpdate();
	}
	
	public void updateBlock(ConferenceEntity owningConference, BlockEntity blockToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisBlocksConference(owningConference, blockToUpdate,crsLoggedInUser);
		
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(blockQueries.update())
				.addParameter("id", blockToUpdate.getId())
				.addParameter("pageId", blockToUpdate.getPageId())
				.addParameter("position", blockToUpdate.getPosition())
				.addParameter("blockType", blockToUpdate.getBlockType())
				.addParameter("adminOnly", blockToUpdate.isAdminOnly())
				.addParameter("required", blockToUpdate.isRequired())
				.addParameter("title", blockToUpdate.getTitle())
				.executeUpdate();
	}

	public void deleteBlock(ConferenceEntity owningConference, UUID blockId, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		BlockEntity blockToDelete = fetchBlockBy(blockId);

		verifyUserIdHasAccessToModifyThisBlocksConference(owningConference, blockToDelete,crsLoggedInUser);

		deleteBlock(blockToDelete);
	}

	protected void deleteBlock(BlockEntity blockEntity) throws UnauthorizedException
	{
		answerService.deleteAnswersByBlockId(blockEntity.getId());

		sql.createQuery(blockQueries.delete())
				.addParameter("blockId", blockEntity.getId())
				.executeUpdate();
	}

	private void verifyUserIdHasAccessToModifyThisBlocksConference(ConferenceEntity owningConference, BlockEntity blockToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		/*This could NPE if the conference is null, I considered adding an extra check here, but
		 * the end result would only be that a different exception be thrown.  Either way it
		 * will end up as a 500 to the client.*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(owningConference.getContactPersonId()))
		{
			throw new UnauthorizedException();
		}
		
	}
}
