package org.cru.crs.api.model.answer;

import org.ccci.util.strings.Strings;
import org.codehaus.jackson.annotate.JsonIgnore;

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

	@JsonIgnore
	public boolean isEmpty()
	{
		return Strings.isEmpty(text);
	}
}
