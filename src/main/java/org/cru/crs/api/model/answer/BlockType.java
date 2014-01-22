package org.cru.crs.api.model.answer;

/**
 * This enumerated type provides mapping to possible BlockEntity blockType values.
 */
public enum BlockType
{
	EMAIL_QUESTION("emailQuestion"),
	NAME_QUESTION("nameQuestion"),
	ADDRESS_QUESTION("addressQuestion"),
	PHONE_QUESTION("phoneQuestion"),
	NUMBER_QUESTION("numberQuestion"),
	DATE_QUESTION("dateQuestion")
	;

	public static BlockType fromString(String blockType)
	{
		if(blockType.equals(EMAIL_QUESTION.toString()))
			return EMAIL_QUESTION;

		if(blockType.equals(NAME_QUESTION.toString()))
			return NAME_QUESTION;

		if(blockType.equals(ADDRESS_QUESTION.toString()))
			return ADDRESS_QUESTION;

		if(blockType.equals(PHONE_QUESTION.toString()))
			return PHONE_QUESTION;

		if(blockType.equals(NUMBER_QUESTION.toString()))
			return NUMBER_QUESTION;

		if(blockType.equals(DATE_QUESTION.toString()))
			return DATE_QUESTION;

		throw new RuntimeException("Could not find block type for " + blockType);
	}

	private BlockType(final String type)
	{
		this.type = type;
	}

	private final String type;

	public boolean isTextQuestion()
	{
		return this.equals(EMAIL_QUESTION) || this.equals(PHONE_QUESTION) || this.equals(NUMBER_QUESTION);
	}

	public boolean isDateQuestion()
	{
		return this.equals(DATE_QUESTION);
	}

	public boolean isNameQuestion()
	{
		return this.equals(NAME_QUESTION);
	}

	public boolean isAddressQuestion()
	{
		return this.equals(ADDRESS_QUESTION);
	}

	@Override
	public String toString()
	{
		return type;
	}
}