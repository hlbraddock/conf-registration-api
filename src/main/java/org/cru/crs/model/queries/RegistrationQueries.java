package org.cru.crs.model.queries;

public class RegistrationQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM registrations WHERE id = :id");
		
		return query.toString();
	}

	public String selectAllForConference()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM registrations WHERE conference_id = :conferenceId");
		
		return query.toString();
	}
	
	public String selectByUserIdConferenceId()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM regstrations WHERE ")
				.append("user_id = :userID")
				.append(" AND ")
				.append("conference_id = :conferenceId");
		
		return query.toString();
	}
	
	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE registrations SET ")
				.append("user_id = :userId,")
				.append("conference_id = :conferenceId")
				.append("completed = :completed")
				.append(" WHERE ")
				.append("id = :id");
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO registrations(")
				.append("id,")
				.append("user_id,")
				.append("conference_id,")
				.append("completed")
				.append(") VALUES(")
				.append(":id,")
				.append(":userId,")
				.append(":conferenceId,")
				.append(":completed")
				.append(")");
		return null;
	}

	@Override
	public String delete()
	{
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM registrations WHERE id = :id");
		
		return query.toString();
	}

}
