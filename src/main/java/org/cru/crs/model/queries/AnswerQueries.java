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
	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE answers SET ")
				.append("registration_id = :registrationId")
				.append("block_id = :blockId")
//				.append("json = :json")
				;
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO answers()")
				.append("id,")
				.append("registration_id,")
				.append("block_id")
//				.append("json")
				.append(") VALUES (")
				.append(":id,")
				.append(":registrationId,")
				.append(":blockId")
//				.append(":json")
				;
		
		return query.toString();
	}

	@Override
	public String delete()
	{
		return "DELETE FROM answers WHERE id = :id";
	}

}
