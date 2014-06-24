package org.cru.crs.jaxrs;

import com.fasterxml.jackson.JsonGenerationException;
import com.fasterxml.jackson.JsonParseException;
import com.fasterxml.jackson.map.JsonMappingException;
import com.fasterxml.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Conference;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class JsonDateTimeTest
{

	@Test(groups="unittest")
	public void testSerializeDateTime() throws JsonGenerationException, JsonMappingException, IOException
	{
		Conference conf = new Conference();
		conf.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 4, 10, 0, 30, 45));
		
		ObjectMapper mapper = new ObjectMapper();
		
		Assert.assertTrue(mapper.writeValueAsString(conf).contains("\"eventStartTime\":\"2013-04-10T00:30:45.000Z\"") );
	}
	
	@Test(groups="unittest")
	public void testDeserializeDateTime() throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		Conference conf = mapper.readValue("{\"eventStartTime\":\"2013-04-10T00:30:45.000Z\"}", Conference.class);
		
		Assert.assertEquals(conf.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013, 4, 10, 0, 30, 45));
	}
	
}
