package org.cru.crs.model.queries;

public class SessionQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM sessions WHERE id = :id");
		
		return query.toString();
	}

	public String selectByAuthProviderId()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM sessions WHERE auth_provider_id = :authProviderId");
		
		return query.toString();
	}
	
	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE sessions SET ")
				.append("auth_code = :authCode")
				.append("expiration = :expiration")
				.append("auth_provider_id = :authProviderId")
				.append(" WHERE ")
				.append("id = :id");
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO sessions(")
				.append("id,")
				.append("auth_provider_id,")
				.append("expiration,")
				.append("auth_code")
				.append(") VALUES( ")
				.append(":id,")
				.append(":authProviderId,")
				.append(":expiration,")
				.append(":authCode")
				.append(")");
		
		return query.toString();
	}

	@Override
	public String delete()
	{
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM sessions WHERE id = :id");
		
		return query.toString();
	}

}
