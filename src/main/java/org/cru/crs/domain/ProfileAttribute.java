package org.cru.crs.domain;

import org.ccci.util.ValueObject;
import org.codehaus.jackson.JsonNode;

/**
 * @ lee.braddock
 */
public class ProfileAttribute extends ValueObject
{
	public enum Type
	{
		EMAIL, NAME, PHONE, ADDRESS, BIRTH_DATE, GENDER, CAMPUS, GRADUATION, DORMITORY
	}

	private Type type;
	private JsonNode value;

	public ProfileAttribute(Type type, JsonNode value)
	{
		this.type = type;
		this.value = value;
	}

	public Type getType()
	{
		return type;
	}

	public JsonNode getValue()
	{
		return value;
	}

	@Override
	protected Object[] getComponents()
	{
		return new Object[]{type, value};
	}
}