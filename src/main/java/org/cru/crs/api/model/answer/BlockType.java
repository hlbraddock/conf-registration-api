package org.cru.crs.api.model.answer;

public enum BlockType
{
	ADDRESS_QUESTION("addressQuestion", Format.JSON),
	DATE_QUESTION("dateQuestion", Format.TEXT),
	EMAIL_QUESTION("emailQuestion", Format.TEXT),
	GENDER_QUESTION("genderQuestion", Format.TEXT),
	NAME_QUESTION("nameQuestion", Format.JSON),
	NUMBER_QUESTION("numberQuestion", Format.TEXT),
	PHONE_QUESTION("phoneQuestion", Format.TEXT),
	RADIO_QUESTION("radioQuestion", Format.TEXT),
	SELECT_QUESTION("selectQuestion", Format.JSON),
	TEXT_QUESTION("textQuestion", Format.TEXT)
	;

	public enum Format
	{
		JSON, TEXT
	}

	public static BlockType fromString(String blockTypeString)
	{
		for (BlockType blockType : BlockType.values()) {
			if(blockType.type.equals(blockTypeString))
				return blockType;
		}

		throw new RuntimeException("Could not find block type for " + blockTypeString);
	}

	private final String type;
	private final Format format;

	private BlockType(final String type, final Format format)
	{
		this.type = type;
		this.format = format;
	}

	public String getType()
	{
		return type;
	}

	public boolean isJsonFormat()
	{
		return format.equals(Format.JSON);
	}

	public boolean isTextFormat()
	{
		return format.equals(Format.TEXT);
	}
}