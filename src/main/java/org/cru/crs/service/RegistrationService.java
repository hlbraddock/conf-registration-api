package org.cru.crs.service;

import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.authz.OperationType;
import org.cru.crs.authz.RegistrationAuthorization;
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

    @Inject
    public RegistrationService(EntityManager em)
    {
        this.em = em;
    }

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		TypedQuery<RegistrationEntity> query = em.createQuery("SELECT registration " +
																"FROM RegistrationEntity registration " +
																"WHERE registration.conference.id = :conference_id", RegistrationEntity.class);

		query.setParameter("conference_id", conferenceId);

		HashSet<RegistrationEntity> registrationEntities = new HashSet<RegistrationEntity>(query.getResultList());

		// if authorized as admin for any one registration then authorized for all
		if(getAnyOne(registrationEntities) != null)
			RegistrationAuthorization.authorize(getAnyOne(registrationEntities), crsApplicationUser, OperationType.ADMIN);

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

		RegistrationAuthorization.authorize(registrationEntity, crsApplicationUser, OperationType.READ);

		return registrationEntity;
	}

	public RegistrationEntity getRegistrationBy(UUID registrationId, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
        RegistrationEntity registrationEntity = em.find(RegistrationEntity.class, registrationId);

		RegistrationAuthorization.authorize(registrationEntity, crsApplicationUser, OperationType.READ);

		return registrationEntity;
    }

    public void createNewRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		RegistrationAuthorization.authorize(registrationEntity, crsApplicationUser, OperationType.CREATE);

		em.persist(registrationEntity);
    }

    public void updateRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		RegistrationAuthorization.authorize(registrationEntity, crsApplicationUser, OperationType.UPDATE);

		em.merge(registrationEntity);
    }

    public void deleteRegistration(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		RegistrationAuthorization.authorize(registrationEntity, crsApplicationUser, OperationType.DELETE);

		em.remove(registrationEntity);
    }

	private <T> T getAnyOne(Set<T> elements)
	{
		for(T t : elements)
			return t;

		return null;
	}
}
