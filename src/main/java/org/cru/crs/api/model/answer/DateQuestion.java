package org.cru.crs.api.model.answer;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
