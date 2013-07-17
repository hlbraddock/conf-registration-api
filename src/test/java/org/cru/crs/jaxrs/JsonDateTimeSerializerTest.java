package org.cru.crs.jaxrs;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JsonDateTimeSerializerTest
{

	@Test(groups="unittest")
	public void testSerializeDateTime() throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		
		Assert.assertEquals(mapper.writeValueAsBytes(DateTimeCreaterHelper.createDateTime(2013, 4, 10, 0, 30, 45)), "2013-04-10T00:30:45");
	}
}
