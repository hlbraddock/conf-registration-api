package org.cru.crs.auth.api;

import org.ccci.util.time.Clock;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsPropertiesFactory;

/**
 * User: lee.braddock
 */
public class TestAuthManager extends AbstractAuthManager
{
	public static TestAuthManager getInstance(SessionService sessionService, AuthenticationProviderService authenticationProviderService, Clock clock)
	{
		TestAuthManager testAuthManager = new TestAuthManager();
		testAuthManager.crsProperties = new CrsPropertiesFactory().get();
		testAuthManager.sessionService = sessionService;
		testAuthManager.authenticationProviderService = authenticationProviderService;
		testAuthManager.clock = clock;

		return testAuthManager;
	}

	public String login(AuthenticationProviderUser authenticationProviderUser)
	{
		authenticationProviderService.createIdentityAndAuthProviderRecords(authenticationProviderUser);

		String authCode = AuthCodeGenerator.generate();

		persistSession(authenticationProviderUser, authCode);

		return authCode;
	}
}
