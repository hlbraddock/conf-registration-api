package org.cru.crs.auth.api;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import java.util.List;
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

	private Logger logger = Logger.getLogger(AbstractAuthManager.class);

	protected void persistIdentityAndAuthProviderRecordsIfNecessary(AuthenticationProviderUser user)
	{
		if (authenticationProviderService.findAuthProviderIdentityByUserAuthProviderId(user.getId()) == null)
		{
			authenticationProviderService.createIdentityAndAuthProviderRecords(user);
		}
	}

	protected SessionEntity persistSession(AuthenticationProviderUser authenticationProviderUser)
	{
		AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity =
				authenticationProviderService.findAuthProviderIdentityByUserAuthProviderId(authenticationProviderUser.getId());

		if(authenticationProviderIdentityEntity == null)
		{
			throw new RuntimeException("could not get authentication provider identity for user " + authenticationProviderUser.getUsername());
		}
		return getSession(authenticationProviderIdentityEntity);
	}

	private SessionEntity getSession(AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity)
	{
		SessionEntity sessionEntity = getActiveSession(authenticationProviderIdentityEntity.getId());

		if(sessionEntity != null)
		{
			logger.info("getSession() : returning active session auth code " + sessionEntity.getAuthCode() + ", " + sessionEntity.getExpiration());
			return sessionEntity;
		}

		sessionEntity = createSessionEntity(authenticationProviderIdentityEntity);

		logger.info("getSession() : returning new session auth code " + sessionEntity.getAuthCode() + ", " + sessionEntity.getExpiration());

		return sessionEntity;
	}

	private SessionEntity createSessionEntity(AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity)
	{
		SessionEntity sessionEntity = new SessionEntity();

		sessionEntity.setId(UUID.randomUUID());
		sessionEntity.setAuthCode(AuthCodeGenerator.generate());
		sessionEntity.setAuthProviderId(authenticationProviderIdentityEntity.getId());
		sessionEntity.setExpiration(clock.currentDateTime().plusHours(getMaxSessionLength()));

		sessionService.create(sessionEntity);

		return sessionEntity;
	}

	private SessionEntity getActiveSession(UUID authProviderId)
	{
		List<SessionEntity> sessionEntities = sessionService.fetchSessionsByUserAuthProviderId(authProviderId);

		DateTime expired = clock.currentDateTime().minusHours(getMaxSessionLength());

		for(SessionEntity sessionEntity : sessionEntities)
		{
			if(sessionEntity.getExpiration().isAfter(expired))
			{
				sessionEntity.setExpiration(clock.currentDateTime().plusHours(getMaxSessionLength()));

				return sessionEntity;
			}
		}

		return null;
	}

	private Integer getMaxSessionLength()
	{
		return Simply.toInteger(crsProperties.getProperty("maxSessionLength"), 4);
	}
}
