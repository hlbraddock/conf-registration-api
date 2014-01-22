package org.cru.crs.api.model.answer;

import org.ccci.util.strings.Strings;

public class TextQuestion
{
	private String text;

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public boolean isEmpty()
	{
		return Strings.isEmpty(text);
	}
}
