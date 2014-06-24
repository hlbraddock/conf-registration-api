package org.cru.crs.utils;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

/**
 * @author leebraddock
 */
public class JsonNodeHelper
{
	 // since 2.1 use mapper.getFactory() instead

	public static JsonNode toJsonNode(String jsonText) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(mapper.getJsonFactory().createJsonParser(jsonText));
	}
	
	public static String toJsonString(JsonNode jsonNode) throws IOException
	{
		String jsonText = new ObjectMapper().writeValueAsString(jsonNode);
		if(jsonText == null || jsonText.trim().length() == 0) jsonText="{}";
		return jsonText;
	}

	/**
	 * This will format a string such that the result can be passed to {@link #toJsonNode(String)}
	 */
	public static String toJsonString(String text) throws IOException
	{
		return "\"" + text + "\"";
	}

	public static <T> JsonNode serialize(T t) throws IOException
	{
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		String jsonText = objectWriter.writeValueAsString(t);

		return JsonNodeHelper.toJsonNode(jsonText);
	}

	public static <T> T deserialize(JsonNode jsonNode, Class<T> tClass) throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper.treeToValue(jsonNode, tClass);
	}
}