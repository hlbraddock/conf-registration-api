package org.cru.crs.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.model.RegistrationEntity;

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

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId, CrsApplicationUser loggedInUser)
	{
		//TODO: this needs to verify user has access to get registrations!
		TypedQuery<RegistrationEntity> query = em.createQuery("SELECT registration " +
																"FROM RegistrationEntity registration " +
																"WHERE registration.conference.id = :conference_id", RegistrationEntity.class);

		query.setParameter("conference_id", conferenceId);

		return new HashSet<RegistrationEntity>(query.getResultList());
	}

	public RegistrationEntity getRegistrationByConferenceIdUserId(UUID conferenceId, UUID userId)
	{
		try
		{
			return em.createQuery("SELECT registration FROM RegistrationEntity registration" +
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
	}

	public RegistrationEntity getRegistrationBy(UUID registrationId)
    {
        return em.find(RegistrationEntity.class, registrationId);
    }

    public void createNewRegistration(RegistrationEntity newRegistration)
    {
        em.persist(newRegistration);
    }

    public void updateRegistration(RegistrationEntity registrationToUpdate)
    {
        em.merge(registrationToUpdate);
    }

    public void deleteRegistration(RegistrationEntity registrationToDelete)
    {
        em.remove(registrationToDelete);
    }
}
