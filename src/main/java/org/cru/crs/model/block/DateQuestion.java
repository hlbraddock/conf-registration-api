package org.cru.crs.model.block;

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

	public boolean isEmpty()
	{
		return text == null;
	}
}
