package org.cru.crs.jaxrs;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class JsonStandardDateTimeSerializer extends JsonSerializer<DateTime>
{

	private static final DateTimeFormatter iso8601Formatter = ISODateTimeFormat.dateTime().withZoneUTC();
	
	@Override
	public void serialize(DateTime datetime, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException
	{
		String jsonFormattedDatetime = iso8601Formatter.print(datetime);
		jGen.writeString(jsonFormattedDatetime);
	}

}
