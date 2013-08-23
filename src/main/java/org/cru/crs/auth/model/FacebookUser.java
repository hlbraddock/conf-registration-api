package org.cru.crs.auth.model;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.auth.AuthenticationProviderType;

/**
 * User: Lee_Braddock
 */
public class FacebookUser extends AuthenticationProviderUser
{
	private String fullName;
	
	public static FacebookUser fromJsonNode(JsonNode user)
	{
		FacebookUser facebookUser = new FacebookUser();

		facebookUser.id = user.get("id").toString();
		facebookUser.fullName = user.get("name").toString();
		facebookUser.firstName = user.get("first_name").toString();
		facebookUser.lastName = user.get("last_name").toString();
		facebookUser.username = user.get("username").toString();
		facebookUser.authentcationProviderType = AuthenticationProviderType.FACEBOOK;
		
		return facebookUser;
	}

	public String getFullName()
	{
		return this.fullName;
	}
}
