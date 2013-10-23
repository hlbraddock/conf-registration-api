package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.sql2o.Sql2o;

public class BlockService
{
	Sql2o sql;
	PageService pageService;
	ConferenceService conferenceService;
	AnswerService answerService;

    @Inject
	public BlockService(EntityManager em, ConferenceService conferenceService, PageService pageService, AnswerService answerService)
	{
    	this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.conferenceService = conferenceService;
		this.pageService = pageService;
		this.answerService = answerService;
	}
	
	public BlockEntity fetchBlockBy(UUID blockId)
	{
		return sql.createQuery("SELECT * FROM blocks WHERE id = :id", false)
						.addParameter("id", blockId)
						.executeAndFetchFirst(BlockEntity.class);
	}
	
	public List<BlockEntity> fetchBlocksForPage(UUID pageId)
	{
		return sql.createQuery("SELECT * FROM blocks WHERE page_id = :pageId", false)
						.addParameter("pageId", pageId)
						.executeAndFetch(BlockEntity.class);
	}
	
	public void saveBlock(BlockEntity blockToSave)
	{
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery("INSERT INTO blocks(id,page_id,position,block_type,admin_only,required,title VALUES (:id, :pageId, :position, :blockType, :adminOnly, :required, :title")
				.addParameter("id", blockToSave.getId())
				.addParameter("pageId", blockToSave.getPageId())
				.addParameter("position", blockToSave.getPosition())
				.addParameter("block_type", blockToSave.getBlockType())
				.addParameter("admin_only", blockToSave.isAdminOnly())
				.addParameter("required", blockToSave.isRequired())
				.addParameter("title", blockToSave.getTitle())
				.executeUpdate();
	}
	
	public void updateBlock(BlockEntity blockToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		verifyUserIdHasAccessToModifyThisBlocksConference(blockToUpdate,crsLoggedInUser);
		
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery("UPDATE blocks SET page_id = :pageId, position = :position, block_type = :blockType, admin_only = :adminOnly, required = :required, title = :title WHERE id = :id")
				.addParameter("id", blockToUpdate.getId())
				.addParameter("pageId", blockToUpdate.getPageId())
				.addParameter("position", blockToUpdate.getPosition())
				.addParameter("block_type", blockToUpdate.getBlockType())
				.addParameter("admin_only", blockToUpdate.isAdminOnly())
				.addParameter("required", blockToUpdate.isRequired())
				.addParameter("title", blockToUpdate.getTitle())
				.executeUpdate();
	}

	public void deleteBlock(UUID blockId, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		BlockEntity blockToDelete = fetchBlockBy(blockId);

		verifyUserIdHasAccessToModifyThisBlocksConference(blockToDelete,crsLoggedInUser);

		deleteBlock(blockToDelete);
	}

	protected void deleteBlock(BlockEntity blockEntity) throws UnauthorizedException
	{
		answerService.deleteAnswersByBlockId(blockEntity.getId());

		sql.createQuery("DELETE from blocks where id = :blockId")
				.addParameter("blockId", blockEntity.getId())
				.executeUpdate();
	}

	private void verifyUserIdHasAccessToModifyThisBlocksConference(BlockEntity blockToUpdate, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		PageEntity pageBlockBelongsTo = pageService.fetchPageBy(blockToUpdate.getPageId());
		ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(pageBlockBelongsTo.getConferenceId());
		
		/*This could NPE if the conference is null, I considered adding an extra check here, but
		 * the end result would only be that a different exception be thrown.  Either way it
		 * will end up as a 500 to the client.*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferencePageBelongsTo.getContactPersonId()))
		{
			throw new UnauthorizedException();
		}
		
	}
}
