package org.cru.crs.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
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
	static ObjectMapper mapper = new ObjectMapper();
	static JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead

	public static JsonNode toJsonNode(String jsonText) throws JsonProcessingException, IOException
	{
		return mapper.readTree(factory.createJsonParser(jsonText));
	}
	
	public static String toJsonString(JsonNode jsonNode) throws JsonGenerationException, JsonMappingException, IOException
	{
		String jsonText = mapper.writeValueAsString(jsonNode);
		if(jsonText == null || jsonText.trim().length() == 0) jsonText="{}";
		return jsonText;
	}
}
