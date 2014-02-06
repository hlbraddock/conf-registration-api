package org.cru.crs.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class CrsPropertiesFactory
{
	private static String propertiesFile = "/apps/apps-config/conf-registration-api-properties.xml";
	private static String propertiesFile2 = "/default-properties.xml";

	@Produces
	public CrsProperties get()
	{
		return new CrsProperties(null, false, propertiesFile, propertiesFile2);
	}
}
