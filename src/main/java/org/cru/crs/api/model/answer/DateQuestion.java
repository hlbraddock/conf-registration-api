package org.cru.crs.api.model.answer;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.joda.time.DateTime;

public class DateQuestion
{
	private DateTime text = null;

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getText()
	{
		return text;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
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
