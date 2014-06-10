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
		this.id = getAttributeValue(user, "id");
		this.fullName = getAttributeValue(user, "name");
		this.firstName = getAttributeValue(user, "first_name");
		this.lastName = getAttributeValue(user, "last_name");
		this.username = getAttributeValue(user, "username");
		this.email = getAttributeValue(user, "email");
		this.accessToken = accessToken;
		this.authenticationProviderType = AuthenticationProviderType.FACEBOOK;
	}

	private String getAttributeValue(JsonNode jsonNode, String attribute)
	{
		try
		{
			// Facebook attribute values are (unnecessarily, for us) in quotes
			return jsonNode.get(attribute).toString().trim().replace("\"", "");
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public String getFullName()
	{
		return this.fullName;
	}
}
