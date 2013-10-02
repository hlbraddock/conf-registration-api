package org.cru.crs.auth.api;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.Simply;
import org.joda.time.DateTime;

import java.util.UUID;

public abstract class AbstractAuthManager
{
	@Inject
	CrsProperties crsProperties;

	@Inject
	AuthenticationProviderService authenticationProviderService;

	@Inject
	SessionService sessionService;

	@Inject
	Clock clock;

	protected void persistIdentityAndAuthProviderRecordsIfNecessary(AuthenticationProviderUser user)
	{
		if (authenticationProviderService.findAuthProviderIdentityByAuthProviderId(user.getId()) == null)
		{
			authenticationProviderService.createIdentityAndAuthProviderRecords(user);
		}
	}

	protected void persistSession(AuthenticationProviderUser authenticationProviderUser, String authCode)
	{
		SessionEntity sessionEntity = new SessionEntity();

		AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity =
				authenticationProviderService.findAuthProviderIdentityByAuthProviderId(authenticationProviderUser.getId());

		if(authenticationProviderIdentityEntity == null)
			throw new RuntimeException("could not get authentication provider identity for user " + authenticationProviderUser.getUsername());


		DateTime expiration = clock.currentDateTime().plusHours(Simply.toInteger(crsProperties.getProperty("maxSessionLength"), 4));

		sessionEntity.setId(UUID.randomUUID());
		sessionEntity.setAuthCode(authCode);
		sessionEntity.setAuthenticationProviderIdentityEntity(authenticationProviderIdentityEntity);
		sessionEntity.setExpiration(expiration);

		sessionService.create(sessionEntity);
	}
}
