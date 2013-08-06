package org.cru.crs.utils;

import org.ccci.util.properties.PropertiesWithFallback;

public class CrsProperties extends PropertiesWithFallback
{
	private static String propertiesFile = "/apps/apps-config/crs-conf-api-properties.xml";
	private static String propertiesFile2 = "/default-properties.xml";

	private static CrsProperties instance = null;

	public static CrsProperties getInstance()
	{
		if(instance == null)
			instance = new CrsProperties();

		return instance;
	}

	public CrsProperties()
	{
		super(null, false, propertiesFile, propertiesFile2);
	}
}
