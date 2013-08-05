package org.cru.crs.auth;

import org.codehaus.jackson.JsonNode;

/**
 * User: Lee_Braddock
 */
public class FacebookUser
{
	private String id;
	private String fullName;
	private String firstName;
	private String lastName;

	public static FacebookUser fromJsonNode(JsonNode user)
	{
		FacebookUser facebookUser = new FacebookUser();

		facebookUser.setId(user.get("id").toString());
		facebookUser.setFullName(user.get("name").toString());
		facebookUser.setFirstName(user.get("first_name").toString());
		facebookUser.setLastName(user.get("last_name").toString());

		return facebookUser;
	}

	@Override
	public String toString()
	{
		return getId();
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}
