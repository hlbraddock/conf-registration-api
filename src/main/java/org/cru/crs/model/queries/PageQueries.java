package org.cru.crs.model.queries;

public class PageQueries implements BasicQueries
{
	public String selectById()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM pages WHERE id = :id");
		
		return query.toString();
	}
	
	public String selectAllForConference()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM pages WHERE conference_id = :conferenceId");
		
		return query.toString();
	}
	
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE pages SET ")
				.append("title = :title,")
				.append("position = :position,")
				.append("conference_id = :conferenceId")
				.append(" WHERE ")
				.append("id = :id");
		
		return query.toString();
	}
	
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO pages(")
				.append("id,")
				.append("title,")
				.append("conference_id,")
				.append("position")
				.append(") VALUES(")
				.append(":id,")
				.append(":title,")
				.append(":conferenceId,")
				.append(":position")
				.append(")");
		
		return query.toString();
	}
	
	public String delete()
	{
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM pages WHERE id = :id;");
		
		return query.toString();
	}
}
