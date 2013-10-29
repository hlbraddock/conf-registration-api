package org.cru.crs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.cru.crs.model.queries.RegistrationQueries;
import org.cru.crs.utils.CollectionUtils;
import org.jboss.logging.Logger;
import org.sql2o.Sql2o;

/**
 * User: lee.braddock
 */
public class RegistrationService
{

	Sql2o sql;
	
	AuthorizationService authorizationService;
	ConferenceService conferenceService; 

	RegistrationQueries registrationQueries = new RegistrationQueries();
	
	private Logger logger = Logger.getLogger(RegistrationService.class);

	@Inject
    public RegistrationService(Sql2o sql, ConferenceService conferenceService, AuthorizationService authorizationService)
    {
		this.sql = sql;
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(RegistrationEntity.class));
		this.authorizationService = authorizationService;
		this.conferenceService = conferenceService;
    }

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		List<RegistrationEntity> registrations = sql.createQuery(registrationQueries.selectAllForConference())
														.addParameter("conferenceId", conferenceId)
														.executeAndFetch(RegistrationEntity.class);

		Set<RegistrationEntity> regstrationsAsSet = new HashSet<RegistrationEntity>(registrations);
		
		// if authorized as admin for any one registration then authorized for all
		if(registrations.size() > 0)
		{
			authorizationService.authorize(CollectionUtils.getAnyOne(regstrationsAsSet), conferenceService.fetchConferenceBy(conferenceId), OperationType.ADMIN, crsApplicationUser);
		}
		return regstrationsAsSet;
	}

	public RegistrationEntity getRegistrationByConferenceIdUserId(UUID conferenceId, UUID userId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{

		RegistrationEntity registration = sql.createQuery(registrationQueries.selectByUserIdConferenceId())
												.addParameter("conferenceId", conferenceId)
												.addParameter("userId", userId)
												.executeAndFetchFirst(RegistrationEntity.class);

		authorizationService.authorize(registration, conferenceService.fetchConferenceBy(conferenceId), OperationType.READ, crsApplicationUser);

		return registration;
	}

	public RegistrationEntity getRegistrationBy(UUID registrationId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}
		
		logger.info("get registration by " + registrationId + " with user " + crsApplicationUser.getAuthProviderUsername());

		RegistrationEntity registration = sql.createQuery(registrationQueries.selectById())
												.addParameter("id", registrationId)
												.executeAndFetchFirst(RegistrationEntity.class);
		if(registration == null)
		{
			return registration;
		}

		logger.info("get registration by " + registrationId + " with user " + crsApplicationUser + " checking authorization");

		authorizationService.authorize(registration,conferenceService.fetchConferenceBy(registration.getConferenceId()), OperationType.READ, crsApplicationUser);

		logger.info("get registration by is authorized");

		return registration;
    }

    public void createNewRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		if(isUserRegisteredForConference(crsApplicationUser, registrationEntity.getConferenceId()))
		{
			throw new UnauthorizedException();
		}

		authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),OperationType.CREATE, crsApplicationUser);

        registrationEntity.setCompleted(false); //they're just starting, so clearly it's not complete
		if(registrationEntity.getId() == null) registrationEntity.setId(UUID.randomUUID());
			
		sql.createQuery(registrationQueries.insert())
				.addParameter("id", registrationEntity.getId())
				.addParameter("conferenceId", registrationEntity.getConferenceId())
				.addParameter("userId", registrationEntity.getUserId())
				.addParameter("completed", registrationEntity.getCompleted())
				.executeUpdate();
    }

	private boolean isUserRegisteredForConference(CrsApplicationUser crsApplicationUser, UUID conferenceId) throws UnauthorizedException
	{
		return (getRegistrationByConferenceIdUserId(conferenceId, crsApplicationUser.getId(), crsApplicationUser) != null);
	}

	public void updateRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), OperationType.UPDATE, crsApplicationUser);

		sql.createQuery(registrationQueries.update())
					.addParameter("id", registrationEntity.getId())
					.addParameter("conferenceId", registrationEntity.getConferenceId())
					.addParameter("userId", registrationEntity.getUserId())
					.addParameter("completed", registrationEntity.getCompleted())
					.executeUpdate();
    }

    public void deleteRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		authorizationService.authorize(registrationEntity, conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), OperationType.DELETE, crsApplicationUser);

		//TODO: delete answers
		
		sql.createQuery(registrationQueries.delete())
				.addParameter("id", registrationEntity.getId())
				.executeUpdate();
    }
}
