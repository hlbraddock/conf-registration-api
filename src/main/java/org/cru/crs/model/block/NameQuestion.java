package org.cru.crs.model.block;

import org.ccci.util.strings.Strings;

public class NameQuestion
{
	private String firstName;
	private String lastName;

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

	public boolean isEmpty()
	{
		return Strings.isEmpty(firstName) && Strings.isEmpty(lastName);
	}
}
