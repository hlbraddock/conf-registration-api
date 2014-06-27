package org.cru.crs.auth.api;

import org.ccci.util.time.Clock;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.SessionService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import javax.inject.Inject;
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
	UserService userService;

	@Inject
	ProfileService profileService;

	@Inject
	Clock clock;

	private Logger logger = Logger.getLogger(AbstractAuthManager.class);

	protected void persistIdentityAndAuthProviderRecordsIfNecessary(AuthenticationProviderUser authenticationProviderUser)
	{
		if (authenticationProviderService.findAuthProviderIdentityByUserAuthProviderId(authenticationProviderUser.getId()) == null)
		{
			UserEntity userEntity = authenticationProviderUser.toUserEntity();

			userService.createUser(userEntity);

			authenticationProviderService.createAuthProviderRecord(authenticationProviderUser.toAuthProviderIdentityEntity(userEntity.getId()));

			// create initial profile from auth provider data
			profileService.createProfile(authenticationProviderUser.toProfileEntity(userEntity.getId()));
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

				sessionService.update(sessionEntity);

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
