package org.cru.crs.jaxrs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class JsonStandardDateTimeDeserializer extends JsonDeserializer<DateTime>
{
	private static final DateTimeFormatter iso8601Formatter = ISODateTimeFormat.dateTime().withZoneUTC();
	
	@Override
	public DateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException
	{
		return iso8601Formatter.parseDateTime(jsonParser.getText());
	}

}
