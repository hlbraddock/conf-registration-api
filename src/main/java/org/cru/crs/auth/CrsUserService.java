package org.cru.crs.auth;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

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
import org.jboss.resteasy.spi.UnauthorizedException;
import org.joda.time.DateTime;

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

	public CrsApplicationUser getLoggedInUser(String authCode)
	{
		try
		{
			logger.info("getLoggedInUser() " + authCode);

			SessionEntity sessionEntity = sessionService.getSessionByAuthCode(authCode);
			logger.info("getLoggedInUser() session entity is " + sessionEntity);
			Simply.logObject(sessionEntity, CrsApplicationUser.class);

			if(sessionEntity == null)
			{
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
			
			logger.info("getLoggedInUser() is expired " + Session.fromJpa(sessionEntity).isExpired());
			if(Session.fromJpa(sessionEntity).isExpired())
			{
				throw new WebApplicationException(Status.UNAUTHORIZED);
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
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
	}
}
