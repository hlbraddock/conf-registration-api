package org.cru.crs.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.RegistrationViewEntity;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class RegistrationViewService
{

	Connection sqlConnection;
	PageService pageService;
	BlockService blockService;
	
	/*required for weld*/
	public RegistrationViewService() {}
	
	@Inject
	public RegistrationViewService(Connection sqlConnection, PageService pageService, BlockService blockService)
	{
		this.sqlConnection = sqlConnection;
		this.pageService = pageService;
		this.blockService = blockService;
	}
	
	public RegistrationViewEntity getRegistrationViewById(UUID id)
	{
		return sqlConnection.createQuery(RegistrationViewQueries.selectById())
								.addParameter("id", id)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(RegistrationViewEntity.class);
	}
	
	public List<RegistrationViewEntity> getRegistrationViewsForConference(UUID conferenceId)
	{
		return sqlConnection.createQuery(RegistrationViewQueries.selectByConferenceId())
								.addParameter("conferenceId", conferenceId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(RegistrationViewEntity.class);
	}
	
	public void insertRegistrationView(RegistrationViewEntity dataView)
	{
		validateBlockIds(dataView.getVisibleBlockIds(), dataView.getConferenceId());
		
		sqlConnection.createQuery(RegistrationViewQueries.insert())
								.addParameter("id", dataView.getId())
								.addParameter("conferenceId", dataView.getConferenceId())
								.addParameter("createdByUserId", dataView.getCreatedByUserId())
								.addParameter("name", dataView.getName())
								.addParameter("visibleBlockIds", dataView.getVisibleBlockIds())
								.executeUpdate();
	}
	
	public void updateRegistrationView(RegistrationViewEntity dataView)
	{
		validateBlockIds(dataView.getVisibleBlockIds(), dataView.getConferenceId());
		
		sqlConnection.createQuery(RegistrationViewQueries.update())
								.addParameter("id", dataView.getId())
								.addParameter("conferenceId", dataView.getConferenceId())
								.addParameter("createdByUserId", dataView.getCreatedByUserId())
								.addParameter("name", dataView.getName())
								.addParameter("visibleBlockIds", dataView.getVisibleBlockIds())
								.executeUpdate();
	}
	
	public void deleteRegistrationView(UUID id)
	{
		sqlConnection.createQuery(RegistrationViewQueries.delete())
								.addParameter("id", id)
								.executeUpdate();
	}
	
	private void validateBlockIds(JsonNode visibleBlockIds, UUID conferenceId)
	{
		
		for(JsonNode idNode : visibleBlockIds)
		{
			UUID blockId = UUID.fromString(idNode.textValue());
			BlockEntity referencedBlock = blockService.getBlockById(blockId);
			if(referencedBlock == null)
			{
				throw new BadRequestException("blockId " + blockId + " is not a valid block.");
			}
			PageEntity pageOwningReferencedBlock = pageService.getPageById(referencedBlock.getPageId());
						
			if(pageOwningReferencedBlock == null || !pageOwningReferencedBlock.getConferenceId().equals(conferenceId))
			{
				throw new BadRequestException("blockId " + blockId + " is not a valid block.");
			}
		}
	}
	
	private static class RegistrationViewQueries
	{

		private static String selectById()
		{
			return "SELECT * FROM registration_views WHERE id = :id";
		}
		
		private static String selectByConferenceId()
		{
			return "SELECT * FROM registration_views WHERE conference_id = :conferenceId";
		}

		private static String update() 
		{
			return "UPDATE registration_views SET conference_id = :conferenceId, created_by_user_id = :createdByUserId, " +
					"name = :name, visible_block_ids = :visibleBlockIds WHERE id = :id";
		}

		private static String insert()
		{
			return "INSERT INTO registration_views(id, conference_id, created_by_user_id, name, visible_block_ids)" +
					"VALUES(:id, :conferenceId, :createdByUserId, :name, :visibleBlockIds)";
		}

		private static String delete()
		{
			return "DELETE FROM registration_views WHERE id = :id";
		}
		
	}
	
}
