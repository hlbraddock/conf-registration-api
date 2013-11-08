package org.cru.crs.model.queries;

public class AnswerQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM answers WHERE id = :id";
	}

	public String selectAllForBlock()
	{
		return "SELECT * FROM answers WHERE block_id = :blockId";
	}
	
	public String selectAllForRegistration()
	{
		return "SELECT * FROM answers WHERE registration_id = :registrationId";
	}
	
	@Override
	public String update()
	{
		return "UPDATE answers SET " +
				"registration_id = :registrationId, " +
				"block_id = :blockId, " +
				"answer = :answer " +
				" WHERE " +
				"id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO answers(id, registration_id, block_id, answer) VALUES(:id, :registrationId, :blockId, :answer)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM answers WHERE id = :id";
	}

}
