package org.cru.crs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logging.Logger;

import java.io.IOException;

public class Simply
{
	public static Integer toInteger(String string, int defaultValue)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch(Exception e)
		{
			return defaultValue;
		}
	}

	public static void logObject(Object object)
	{
		logObject(object, Simply.class);
	}

	public static void logObject(Object object, Class clazz)
	{
		Logger logger = Logger.getLogger(clazz);

		if(object == null)
			return;

		try
		{
			logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
