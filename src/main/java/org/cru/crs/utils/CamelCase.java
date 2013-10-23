package org.cru.crs.utils;

public class CamelCase
{

	public static String toUnderscore(String s)
	{
		return s.replaceAll(
				String.format("%s|%s|%s",
						"(?<=[A-Z])(?=[A-Z][a-z])",
						"(?<=[^A-Z])(?=[A-Z])",
						"(?<=[A-Za-z])(?=[^A-Za-z])"
						),
						"_"
				);
	}
}
