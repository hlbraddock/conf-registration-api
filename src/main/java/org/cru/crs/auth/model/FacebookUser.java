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
		this.id = toString(user, "id");
		this.fullName = toString(user, "name");
		this.firstName = toString(user, "first_name");
		this.lastName = toString(user, "last_name");
		this.username = toString(user, "username");
		this.email = toString(user, "email");
		this.accessToken = accessToken;
		this.authenticationProviderType = AuthenticationProviderType.FACEBOOK;
	}

	private String toString(JsonNode jsonNode, String attribute)
	{
		// Facebook attribute values are (unnecessarily, for us) in quotes
		return jsonNode.get(attribute).toString().trim().replace("\"", "");
	}

	public String getFullName()
	{
		return this.fullName;
	}
}
