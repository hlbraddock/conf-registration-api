package org.cru.crs.model.queries;

public class RegistrationQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM registrations WHERE id = :id";
	}

	public String selectAllForConference()
	{
		return "SELECT * FROM registrations WHERE conference_id = :conferenceId";
	}

	public String selectCountForConference()
	{
		return "SELECT COUNT(*) FROM registrations WHERE conference_id = :conferenceId";
	}

	public String selectByUserIdConferenceId()
	{
		return "SELECT * FROM registrations WHERE user_id = :userId AND conference_id = :conferenceId";
	}
	
	@Override
	public String update()
	{
		return "UPDATE registrations SET " +
				 "user_id = :userId, " +
				 "conference_id = :conferenceId, " +
				 "total_due = :totalDue, " +
				 "completed = :completed, " +
				 "completed_timestamp = :completedTimestamp " +
				 " WHERE " +
				 "id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO registrations(id, user_id, conference_id, total_due, completed, completed_timestamp) VALUES(:id, :userId, :conferenceId, :totalDue, :completed, :completedTimestamp)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM registrations WHERE id = :id";
	}

}
