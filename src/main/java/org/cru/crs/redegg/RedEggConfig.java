package org.cru.crs.redegg;

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.cru.crs.utils.CrsProperties;
import org.cru.redegg.reporting.errbit.ErrbitConfig;

public class RedEggConfig
{
	
	@Inject CrsProperties properties;
	
	@Produces
	public ErrbitConfig createConfig() throws URISyntaxException
	{
		ErrbitConfig config = new ErrbitConfig();
		config.setEndpoint(new URI("https://errors.uscm.org/notifier_api/v2/notices"));
		config.setKey("f906c1a1ac9d48c8a492580705dbcdd0");
		config.setEnvironmentName(properties.getProperty("errbitEnvironment"));
		
		return config;
	}
}
