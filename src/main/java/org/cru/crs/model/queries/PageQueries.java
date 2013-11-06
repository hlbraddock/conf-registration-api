package org.cru.crs.model.queries;

public class PageQueries implements BasicQueries
{
	public String selectById()
	{
		return "SELECT * FROM pages WHERE id = :id";
	}
	
	public String selectAllForConference()
	{
		return "SELECT * FROM pages WHERE conference_id = :conferenceId";
	}
	
	public String update()
	{
		return "UPDATE pages SET " +
				 "title = :title, " +
				 "position = :position, " +
				 "conference_id = :conferenceId " +
				 " WHERE " +
				 "id = :id";
	}
	
	public String insert()
	{
		return "INSERT INTO pages(id, title, conference_id, position) VALUES(:id, :title, :conferenceId, :position)";
	}
	
	public String delete()
	{
		return "DELETE FROM pages WHERE id = :id";
	}
}
