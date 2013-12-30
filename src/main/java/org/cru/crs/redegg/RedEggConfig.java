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
		config.setEndpoint(new URI(properties.getProperty("errbitEndpoint")));
		config.setKey(properties.getProperty("errbitApiKey"));
		config.setEnvironmentName(properties.getProperty("errbitEnvironment"));
		
		return config;
	}
}
