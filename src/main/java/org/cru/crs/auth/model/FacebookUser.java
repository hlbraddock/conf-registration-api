package org.cru.crs.auth.model;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.auth.AuthenticationProviderType;

/**
 * User: Lee_Braddock
 */
public class FacebookUser extends AuthenticationProviderUser
{
	private String fullName;
	
	public FacebookUser(JsonNode user, String accessToken)
	{
		this.id = user.get("id").toString();
		this.fullName = user.get("name").toString();
		this.firstName = user.get("first_name").toString();
		this.lastName = user.get("last_name").toString();
		this.username = user.get("username").toString();
		this.email = user.get("email").toString();
		this.accessToken = accessToken;
		this.authenticationProviderType = AuthenticationProviderType.FACEBOOK;
	}

	public String getFullName()
	{
		return this.fullName;
	}
}
