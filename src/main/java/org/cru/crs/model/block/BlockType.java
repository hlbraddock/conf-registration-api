package org.cru.crs.model.block;

public enum BlockType
{
	EMAIL_QUESTION("emailQuestion"),
	NAME_QUESTION("nameQuestion"),
	ADDRESS_QUESTION("addressQuestion"),
	PHONE_QUESTION("phoneQuestion"),
	NUMBER_QUESTION("numberQuestion"),
	DATE_QUESTION("dateQuestion")
	;

	private BlockType(final String text)
	{
		this.text = text;
	}

	private final String text;

	@Override
	public String toString()
	{
		return text;
	}

	public static void main(String[] args)
	{
		System.out.println(BlockType.EMAIL_QUESTION);
	}
}