package org.cru.crs.domain;

import org.ccci.util.ValueObject;

/**
 * @ lee.braddock
 */
public class ProfileAttribute extends ValueObject
{
	public enum Type
	{
		None, FirstName, LastName, Phone, Street, City, Zip, Email
	}

	private Type type;
	private String value;

	public ProfileAttribute(Type type, String value)
	{
		this.type = type;
		this.value = value;
	}

	@Override
	protected Object[] getComponents()
	{
		return new Object[]{type, value};
	}

	public Type getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}
}