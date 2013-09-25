package org.cru.crs.auth;

import org.cru.crs.api.model.Session;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.Simply;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.UUID;

public class CrsUserService
{
	@Inject
	SessionService sessionService;

	@Inject
	CrsProperties crsProperties;

	@Inject
	AuthenticationProviderService authenticationProviderService;

	public CrsApplicationUser getLoggedInUser(String authCode) throws UnauthorizedException
	{
		try
		{
			SessionEntity sessionEntity = sessionService.getSessionByAuthCode(authCode);

			if(sessionEntity == null)
				throw new UnauthorizedException();

			if(Session.fromJpa(sessionEntity).isExpired())
				throw new UnauthorizedException();

			UUID authProviderId = sessionEntity.getAuthenticationProviderIdentityEntity().getId();

			AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderId(authProviderId.toString());

			if(authProviderEntity == null)
				throw new UnauthorizedException();

			AuthenticationProviderType authProviderType = AuthenticationProviderType.valueOf(authProviderEntity.getAuthenticationProviderName());

			DateTime expiration = (new DateTime()).plusHours(Simply.toInteger(crsProperties.getProperty("maxSessionLength"), 4));

			sessionEntity.setExpiration(expiration);

			sessionService.update(sessionEntity);

			return new CrsApplicationUser(authProviderEntity.getCrsUser().getId(), authProviderType, authProviderEntity.getUsername());
		}
		catch (Exception e)
		{
			throw new UnauthorizedException(e);
		}
	}
}
