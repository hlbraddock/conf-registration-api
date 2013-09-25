package org.cru.crs.utils;

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
}
