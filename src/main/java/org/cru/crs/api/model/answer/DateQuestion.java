package org.cru.crs.api.model.answer;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;

public class DateQuestion
{
	private DateTime text = null;

	public DateTime getText()
	{
		return text;
	}

	public void setText(DateTime text)
	{
		this.text = text;
	}

	@JsonIgnore
	public boolean isEmpty()
	{
		return text == null;
	}
}
