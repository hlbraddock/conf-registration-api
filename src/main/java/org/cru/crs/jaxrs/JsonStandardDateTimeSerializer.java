package org.cru.crs.jaxrs;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class JsonStandardDateTimeSerializer extends JsonSerializer<DateTime>
{

	private static final DateTimeFormatter iso8601Formatter = ISODateTimeFormat.dateTime();
	
	@Override
	public void serialize(DateTime datetime, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException,JsonProcessingException
	{
		String jsonFormattedDatetime = iso8601Formatter.print(datetime);
		jGen.writeString(jsonFormattedDatetime);
	}

}
