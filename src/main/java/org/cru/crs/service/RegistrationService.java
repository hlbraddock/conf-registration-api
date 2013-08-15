package org.cru.crs.service;

import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.authz.OperationType;
import org.cru.crs.authz.AuthorizationService;
import org.cru.crs.model.RegistrationEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User: lee.braddock
 */
public class RegistrationService {

	EntityManager em;

	AuthorizationService authorizationService;

	@Inject
    public RegistrationService(EntityManager em, AuthorizationService authorizationService)
    {
        this.em = em;
		this.authorizationService = authorizationService;
    }

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		TypedQuery<RegistrationEntity> query = em.createQuery("SELECT registration " +
																"FROM RegistrationEntity registration " +
																"WHERE registration.conference.id = :conference_id", RegistrationEntity.class);

		query.setParameter("conference_id", conferenceId);

		HashSet<RegistrationEntity> registrationEntities = new HashSet<RegistrationEntity>(query.getResultList());

		// if authorized as admin for any one registration then authorized for all
		if(registrationEntities.size() > 0)
			authorizationService.authorize(getAnyOne(registrationEntities), OperationType.ADMIN, crsApplicationUser);

		return registrationEntities;
	}

	public RegistrationEntity getRegistrationByConferenceIdUserId(UUID conferenceId, UUID userId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		RegistrationEntity registrationEntity;

		try
		{
			registrationEntity = em.createQuery("SELECT registration FROM RegistrationEntity registration" +
								" WHERE registration.userId = :user_id " +
									" AND registration.conference.id = :conference_id", RegistrationEntity.class)
						.setParameter("conference_id", conferenceId)
						.setParameter("user_id", userId)
						.getSingleResult();
		}
		catch(NoResultException nre)
		{
			return null;
		}

		authorizationService.authorize(registrationEntity, OperationType.READ, crsApplicationUser);

		return registrationEntity;
	}

	public RegistrationEntity getRegistrationBy(UUID registrationId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
        RegistrationEntity registrationEntity = em.find(RegistrationEntity.class, registrationId);

		authorizationService.authorize(registrationEntity, OperationType.READ, crsApplicationUser);

		return registrationEntity;
    }

    public void createNewRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		authorizationService.authorize(registrationEntity, OperationType.CREATE, crsApplicationUser);

		em.persist(registrationEntity);
    }

    public void updateRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		authorizationService.authorize(registrationEntity, OperationType.UPDATE, crsApplicationUser);

		em.merge(registrationEntity);
    }

    public void deleteRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		authorizationService.authorize(registrationEntity, OperationType.DELETE, crsApplicationUser);

		em.remove(registrationEntity);
    }

	private <T> T getAnyOne(Set<T> elements)
	{
		for(T t : elements)
			return t;

		return null;
	}
}
