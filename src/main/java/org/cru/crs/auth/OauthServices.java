package org.cru.crs.auth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.oauth.OAuthService;

/**
 * User: Lee_Braddock
 */
public class OauthServices
{
	public static OAuthService build(Class<? extends Api> provider, String callback, String apiKey, String apiSecret)
	{
		return new ServiceBuilder()
				.provider(provider)
				.apiKey(apiKey)
				.apiSecret(apiSecret)
				.callback(callback)
				.build();
	}
}
