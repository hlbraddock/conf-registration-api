package org.cru.crs.model.queries;

public class SessionQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM sessions WHERE id = :id";
	}

	public String selectByAuthCode() 
	{
		return "SELECT * FROM sessions WHERE auth_code = :authCode";
	}
	
	public String selectByAuthProviderId()
	{
		return "SELECT * FROM sessions WHERE auth_provider_id = :authProviderId";
	}
	
	@Override
	public String update()
	{
		return "UPDATE sessions SET " +
				 "auth_code = :authCode, " +
				 "expiration = :expiration, " +
				 "auth_provider_id = :authProviderId " +
				 " WHERE " +
				 "id = :id ";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO sessions(id, auth_provider_id, expiration, auth_code) VALUES(:id, :authProviderId, :expiration, :authCode)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM sessions WHERE id = :id";		
	}

}
