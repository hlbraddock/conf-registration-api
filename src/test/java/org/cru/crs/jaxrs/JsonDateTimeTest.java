package org.cru.crs.jaxrs;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Conference;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JsonDateTimeTest
{

	@Test(groups="unittest")
	public void testSerializeDateTime() throws JsonGenerationException, JsonMappingException, IOException
	{
		Conference conf = new Conference();
		conf.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 4, 10, 0, 30, 45));
		
		ObjectMapper mapper = new ObjectMapper();
		
		Assert.assertTrue(mapper.writeValueAsString(conf).contains("\"eventStartTime\":\"2013-04-10T00:30:45.000-04:00\"") );
	}
	
	@Test(groups="unittest")
	public void testDeserializeDateTime() throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		Conference conf = mapper.readValue("{\"eventStartTime\":\"2013-04-10T00:30:45.000-04:00\"}", Conference.class);
		
		Assert.assertEquals(conf.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013, 4, 10, 0, 30, 45));
	}
	
}
