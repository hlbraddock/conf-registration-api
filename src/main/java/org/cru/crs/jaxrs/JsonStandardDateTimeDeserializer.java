package org.cru.crs.jaxrs;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class JsonStandardDateTimeDeserializer extends JsonDeserializer<DateTime> 
{
	private static final DateTimeFormatter iso8601Formatter = ISODateTimeFormat.dateTime();
	
	@Override
	public DateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException
	{
		return iso8601Formatter.parseDateTime(jsonParser.getText());
	}

}
