package org.cru.crs.auth;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Session;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class CrsUserService
{
	@Inject
	SessionService sessionService;

	@Inject
	CrsProperties crsProperties;

	@Inject
	AuthenticationProviderService authenticationProviderService;

	@Inject
	Clock clock;

	private Logger logger = Logger.getLogger(CrsUserService.class);

	public CrsApplicationUser getLoggedInUser(String authCode) throws UnauthorizedException
	{
		try
		{
			logger.info("getLoggedInUser() " + authCode);

			SessionEntity sessionEntity = sessionService.getSessionByAuthCode(authCode);
			logger.info("getLoggedInUser() session entity is " + sessionEntity);
			Simply.logObject(sessionEntity, CrsApplicationUser.class);

			if(sessionEntity == null)
			{
				throw new UnauthorizedException();
			}
			
			logger.info("getLoggedInUser() is expired " + Session.fromJpa(sessionEntity).isExpired());
			if(Session.fromJpa(sessionEntity).isExpired())
			{
				throw new UnauthorizedException();
			}
			
			AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityById(sessionEntity.getAuthProviderId());

			logger.info("getLoggedInUser() auth provider " + authProviderEntity);
			if(authProviderEntity == null)
			{
				throw new UnauthorizedException();
			}
			AuthenticationProviderType authProviderType = AuthenticationProviderType.valueOf(authProviderEntity.getAuthProviderName());

			logger.info("getLoggedInUser() auth provider type " + authProviderType);

			DateTime expiration = clock.currentDateTime().plusHours(Simply.toInteger(crsProperties.getProperty("maxSessionLength"), 4));

			logger.info("getLoggedInUser() update session");

			sessionEntity.setExpiration(expiration);

			logger.info("getLoggedInUser() returning crs application user");

			return new CrsApplicationUser(authProviderEntity.getCrsId(), authProviderType, authProviderEntity.getUsername());
		}
		catch (Exception e)
		{
			logger.info(e);
			throw new UnauthorizedException(e);
		}
	}
}
