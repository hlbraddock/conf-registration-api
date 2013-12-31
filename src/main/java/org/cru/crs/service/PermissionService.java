package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.ccci.util.NotImplementedException;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.queries.PermissionQueries;
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
		return sqlConnection.createQuery(permissionQueries.selectById())
								.addParameter("id", id)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(PermissionEntity.class);
	}
	
	public List<PermissionEntity> getPermissionsForConference(UUID conferenceId)
	{
		return sqlConnection.createQuery(permissionQueries.selectAllForConference())
								.addParameter("conferenceId", conferenceId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(PermissionEntity.class);
	}
	
	public List<PermissionEntity> getPermissionsForUser(UUID userId)
	{
		return sqlConnection.createQuery(permissionQueries.selectAllForUser())
								.addParameter("userId", userId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(PermissionEntity.class);
	}
	
	public PermissionEntity getPermissionForUserOnConference(UUID userId, UUID conferenceId)
	{
		return sqlConnection.createQuery(permissionQueries.selectByUserIdConferenceId())
								.addParameter("userId", userId)
								.addParameter("conferenceId", conferenceId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(PermissionEntity.class);
	}
	
	public void updatePermission(PermissionEntity permissionToUpdate)
	{
		sqlConnection.createQuery(permissionQueries.update())
								.addParameter("id", permissionToUpdate.getId())
								.addParameter("conferenceId", permissionToUpdate.getConferenceId())
								.addParameter("userId", permissionToUpdate.getUserId())
								.addParameter("permissionLevel", (Object)permissionToUpdate.getPermissionLevel())
								.addParameter("givenByUserId", permissionToUpdate.getGivenByUserId())
								.addParameter("lastUpdatedTimestamp", permissionToUpdate.getLastUpdatedTimestamp())
								.executeUpdate();
	}
	
	public void insertPermission(PermissionEntity permissionToInsert)
	{
		sqlConnection.createQuery(permissionQueries.insert())
								.addParameter("id", permissionToInsert.getId())
								.addParameter("conferenceId", permissionToInsert.getConferenceId())
								.addParameter("userId", permissionToInsert.getUserId())
								.addParameter("permissionLevel", (Object)permissionToInsert.getPermissionLevel())
								.addParameter("givenByUserId", permissionToInsert.getGivenByUserId())
								.addParameter("lastUpdatedTimestamp", permissionToInsert.getLastUpdatedTimestamp())
								.executeUpdate();
	}
	
	public void deletePermission(UUID id)
	{
		throw new NotImplementedException();
	}
}
