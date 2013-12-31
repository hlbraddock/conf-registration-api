package org.cru.crs.model.queries;

import org.ccci.util.NotImplementedException;

public class PermissionQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM permissions WHERE id = :id";
	}

	public String selectAllForUser()
	{
		return "SELECT * FROM permissions WHERE user_id = :userId";
	}
	
	public String selectAllForConference()
	{
		return "SELECT * FROM permissions WHERE conference_id = :conferenceId";
	}	

	public String selectByUserIdConferenceId()
	{
		return "SELECT * FROM permissions WHERE user_id = :userId AND conference_id = :conferenceId";
	}
	
	@Override
	public String update()
	{
		return "UPDATE permissions SET " +
					"conference_id = :conferenceId," +
					"user_id = :userId," + 
					"permission_level = :permissionLevel," +
					"given_by_user_id = :givenByUserId," + 
					"last_updated_timestamp = :lastUpdatedTimestamp" +
					" WHERE " +
					"id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO permissions(id, conference_id, user_id, permission_level, given_by_user_id, last_updated_timestamp) " +
				"VALUES(:id, :conferenceId, :userId, :permissionLevel, :givenByUserId, :lastUpdatedTimestamp)";
	}

	@Override
	public String delete()
	{
		throw new NotImplementedException();
	}

}
