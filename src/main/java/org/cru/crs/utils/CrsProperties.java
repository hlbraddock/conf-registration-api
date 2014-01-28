package org.cru.crs.utils;

import org.ccci.util.properties.CcciProperties;
import org.ccci.util.properties.PropertiesWithFallback;

public class CrsProperties extends PropertiesWithFallback
{
	private static final long serialVersionUID = 1L;

	public CrsProperties(CcciProperties.PropertyEncryptionSetup encryptionData, boolean firstSourceOnly, String propertiesFile, String propertiesFile2)
	{
		super(encryptionData, firstSourceOnly, propertiesFile, propertiesFile2);
	}

	public String getNonNullProperty(String property)
	{
		return getProperty(property) == null ? "" : getProperty(property);
	}
}
