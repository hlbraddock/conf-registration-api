package org.cru.crs.model.queries;

public class AuthenticationProviderQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM auth_provider_identities WHERE id = :id";

	}
	
	public String selectByUserAuthProviderId()
	{
		return "SELECT * FROM auth_provider_identities WHERE user_auth_provider_id = :userAuthProviderId";
	}
	
	public String selectByUsernameAuthProviderName()
	{
		return "SELECT * FROM auth_provider_identities WHERE username = :username AND auth_provider_name = :authProviderName";		
	}

	@Override
	public String update()
	{
		return "UPDATE auth_provider_identities SET " +
				 "crs_id = :crsId, " +
				 "user_auth_provider_id = :userAuthProviderId " +
				 "auth_provider_user_access_token = :authProviderUserAccessToken " +
				 "auth_provider_name = :authProviderName " +
				 "username = :username " +
				 "first_name = :firstName " +
				 "last_name = :lastName " +
				 " WHERE  " +
				 "id = :id ";
	}

	public String updateAuthProviderType()
	{
		return "UPDATE auth_provider_identities SET auth_provider_name = :authProviderName WHERE user_auth_provider_id = :userAuthProviderId";
	}
	
	@Override
	public String insert()
	{
		return "INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_user_access_token, auth_provider_name, username, first_name, last_name) " +
				 "VALUES(:id, :crsId, :userAuthProviderId, :authProviderUserAccessToken, :authProviderName, :username, :firstName, :lastName";
		
	}

	@Override
	public String delete()
	{
		return "DELETE FROM auth_provider_identities WHERE id = :id";
	}

}
