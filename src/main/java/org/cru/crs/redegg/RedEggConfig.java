package org.cru.crs.redegg;

import com.google.common.collect.ImmutableList;
import org.cru.crs.utils.CrsProperties;
import org.cru.redegg.recording.api.NoOpParameterSanitizer;
import org.cru.redegg.recording.api.ParameterSanitizer;
import org.cru.redegg.reporting.errbit.ErrbitConfig;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

public class RedEggConfig
{
	
	@Inject CrsProperties properties;
	
	@Produces ParameterSanitizer sanitizer = new NoOpParameterSanitizer();
	
	@Produces
	public ErrbitConfig createConfig() throws URISyntaxException
	{
		ErrbitConfig config = new ErrbitConfig();
		config.setEndpoint(new URI(properties.getProperty("errbitEndpoint")));
		config.setKey(properties.getProperty("errbitApiKey"));
		config.setEnvironmentName(properties.getProperty("errbitEnvironment"));
		
		config.getApplicationBasePackages().addAll(ImmutableList.of("org.cru.crs"));
		
		return config;
	}
}
