package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.PermissionEntity;
import org.sql2o.Connection;

@RequestScoped
public class PermissionService
{
	Connection sqlConnection;
	
	PermissionQueries permissionQueries = new PermissionQueries();
	
	/*required for Weld*/
	public PermissionService()
	{
		
	}
	
	@Inject
	public PermissionService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}
	
	public PermissionEntity getPermissionBy(UUID id)
	{
		return sqlConnection.createQuery(PermissionQueries.selectById())
								.addParameter("id", id)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(PermissionEntity.class);
	}
	
	public PermissionEntity getPermissionByActivationCode(String activationCode)
	{
		return sqlConnection.createQuery(PermissionQueries.selectByActivationCode())
								.addParameter("activationCode", activationCode)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(PermissionEntity.class);
	}
	
	public List<PermissionEntity> getPermissionsForConference(UUID conferenceId)
	{
		return sqlConnection.createQuery(PermissionQueries.selectAllForConference())
								.addParameter("conferenceId", conferenceId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(PermissionEntity.class);
	}
	
	public List<PermissionEntity> getPermissionsForUser(UUID userId)
	{
		return sqlConnection.createQuery(PermissionQueries.selectAllForUser())
								.addParameter("userId", userId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(PermissionEntity.class);
	}
	
	public PermissionEntity getPermissionForUserOnConference(UUID userId, UUID conferenceId)
	{
		return sqlConnection.createQuery(PermissionQueries.selectByUserIdConferenceId())
								.addParameter("userId", userId)
								.addParameter("conferenceId", conferenceId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(PermissionEntity.class);
	}
	
	public void updatePermission(PermissionEntity permissionToUpdate)
	{
		sqlConnection.createQuery(PermissionQueries.update())
								.addParameter("id", permissionToUpdate.getId())
								.addParameter("conferenceId", permissionToUpdate.getConferenceId())
								.addParameter("userId", permissionToUpdate.getUserId())
								.addParameter("emailAddress", permissionToUpdate.getEmailAddress())
								.addParameter("permissionLevel", (Object)permissionToUpdate.getPermissionLevel())
								.addParameter("givenByUserId", permissionToUpdate.getGivenByUserId())
								.addParameter("activationCode", permissionToUpdate.getActivationCode())
								.addParameter("lastUpdatedTimestamp", permissionToUpdate.getLastUpdatedTimestamp())
								.executeUpdate();
	}
	
	public void insertPermission(PermissionEntity permissionToInsert)
	{
		sqlConnection.createQuery(PermissionQueries.insert())
								.addParameter("id", permissionToInsert.getId())
								.addParameter("conferenceId", permissionToInsert.getConferenceId())
								.addParameter("userId", permissionToInsert.getUserId())
								.addParameter("emailAddress", permissionToInsert.getEmailAddress())
								.addParameter("permissionLevel", (Object)permissionToInsert.getPermissionLevel())
								.addParameter("givenByUserId", permissionToInsert.getGivenByUserId())
								.addParameter("activationCode", permissionToInsert.getActivationCode())
								.addParameter("lastUpdatedTimestamp", permissionToInsert.getLastUpdatedTimestamp())
								.executeUpdate();
	}
	
	public void deletePermission(UUID id)
	{
		sqlConnection.createQuery(PermissionQueries.delete())
						.addParameter("id", id)
						.executeUpdate();
	}
	
	private static class PermissionQueries
	{

		private static String selectById()
		{
			return "SELECT * FROM permissions WHERE id = :id";
		}

		private static String selectByActivationCode()
		{
			return "SELECT * FROM permissions WHERE activation_code = :activationCode";
		}
		
		private static String selectAllForUser()
		{
			return "SELECT * FROM permissions WHERE user_id = :userId";
		}
		
		private static String selectAllForConference()
		{
			return "SELECT * FROM permissions WHERE conference_id = :conferenceId";
		}	

		private static String selectByUserIdConferenceId()
		{
			return "SELECT * FROM permissions WHERE user_id = :userId AND conference_id = :conferenceId";
		}
		
		private static String update()
		{
			return "UPDATE permissions SET " +
						"conference_id = :conferenceId," +
						"user_id = :userId," + 
						"email_address = :emailAddress," +
						"permission_level = :permissionLevel," +
						"given_by_user_id = :givenByUserId," + 
						"activation_code = :activationCode," +
						"last_updated_timestamp = :lastUpdatedTimestamp" +
						" WHERE " +
						"id = :id";
		}

		private static String insert()
		{
			return "INSERT INTO permissions(id, conference_id, user_id, email_address, permission_level, given_by_user_id, activation_code, last_updated_timestamp) " +
					"VALUES(:id, :conferenceId, :userId, :emailAddress, :permissionLevel, :givenByUserId, :activationCode, :lastUpdatedTimestamp)";
		}
		
		private static String delete()
		{
			return "DELETE FROM permissions WHERE id = :id";
		}
	}
}
