package org.cru.crs.service;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

/**
 * User: lee.braddock
 */
public class RegistrationService {

    EntityManager em;

    public RegistrationService(EntityManager em)
    {
        this.em = em;
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
