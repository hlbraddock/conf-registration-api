package org.cru.crs.service;

import org.cru.crs.model.RegistrationEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
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

	public Set<RegistrationEntity> fetchAllRegistrations(UUID conferenceId)
	{
		Query query = em.createQuery("SELECT registration FROM RegistrationEntity registration where registration.conference.id = :conference_id", RegistrationEntity.class);

		query.setParameter("conference_id", conferenceId);

		return new HashSet<RegistrationEntity>(query.getResultList());
	}

	public RegistrationEntity getRegistrationByConferenceIdUserId(UUID conferenceId, UUID userId)
	{
		Query query = em.createQuery("SELECT registration FROM RegistrationEntity registration where registration.userId = :user_id and registration.conference.id = :conference_id", RegistrationEntity.class);

		query.setParameter("conference_id", conferenceId);
		query.setParameter("user_id", userId);

		return (RegistrationEntity) query.getSingleResult();
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
