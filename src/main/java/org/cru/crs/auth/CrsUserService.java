package org.cru.crs.auth;

import org.cru.crs.api.model.Session;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;

import javax.inject.Inject;
import java.util.UUID;

public class CrsUserService
{
	@Inject
	SessionService sessionService;

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

			return new CrsApplicationUser(authProviderEntity.getCrsUser().getId(), authProviderType, authProviderEntity.getUsername());
		}
		catch (Exception e)
		{
			throw new UnauthorizedException(e);
		}
	}
}
