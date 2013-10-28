package org.cru.crs.model.queries;

public class AuthenticationProviderQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM auth_provider_identities WHERE id = :id");

		return query.toString();
	}
	
	public String selectByUserAuthProviderId()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM auth_provider_identities WHERE user_auth_provider_id = :userAuthProviderId");
		
		return query.toString();
	}
	
	public String selectByUsernameAuthProviderName()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM auth_provider_identities WHERE ")
				.append("username = :username")
				.append(" AND ")
				.append("auth_provider_name = :authProviderName");
		
		return query.toString();
		
	}

	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE auth_provider_identities SET ")
				.append("crs_id = :crsId,")
				.append("user_auth_provider_id = :userAuthProviderId")
				.append("auth_provider_user_access_token = :authProviderUserAccessToken")
				.append("auth_provider_name = :authProviderName")
				.append("username = :username")
				.append("first_name = :firstName")
				.append("last_name = :lastName")
				.append(" WHERE ")
				.append("id = :id");
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO auth_provider_identities(")
				.append("id,")
				.append("crs_id,")
				.append("user_auth_provider_id,")
				.append("auth_provider_user_access_token,")
				.append("auth_provider_name,")
				.append("username,")
				.append("first_name,")
				.append("last_name")
				.append(") VALUES (")
				.append(":id,")
				.append(":crsId,")
				.append(":userAuthProviderId,")
				.append(":authProviderUserAccessToken,")
				.append(":authProviderName,")
				.append(":username,")
				.append(":firstName,")
				.append(":lastName");
		
		return query.toString();
	}

	@Override
	public String delete()
	{
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM auth_provider_identities WHERE id = :id");
		
		return query.toString();
	}

}
