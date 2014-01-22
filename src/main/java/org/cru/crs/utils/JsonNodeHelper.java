package org.cru.crs.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Only for use in tests, not threadsafe.
 * @author ryancarlson
 *
 */
public class JsonNodeHelper
{
	 // since 2.1 use mapper.getFactory() instead

	public static JsonNode toJsonNode(String jsonText) throws JsonProcessingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(mapper.getJsonFactory().createJsonParser(jsonText));
	}
	
	public static String toJsonString(JsonNode jsonNode) throws JsonGenerationException, JsonMappingException, IOException
	{
		String jsonText = new ObjectMapper().writeValueAsString(jsonNode);
		if(jsonText == null || jsonText.trim().length() == 0) jsonText="{}";
		return jsonText;
	}
}
