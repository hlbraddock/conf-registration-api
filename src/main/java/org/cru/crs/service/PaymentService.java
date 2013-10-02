package org.cru.crs.service;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.model.PaymentEntity;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PaymentService
{
    EntityManager em;

    @Inject
    public PaymentService(EntityManager em)
    {
        this.em = em;
    }

    public PaymentEntity fetchPaymentBy(UUID id)
    {
        return em.find(PaymentEntity.class, id);
    }

    public void createPaymentRecord(PaymentEntity payment)
    {
        em.persist(payment);
    }
    
    public void updatePayment(PaymentEntity payment)
    {
    	em.merge(payment);
    }
    
    public List<PaymentEntity> fetchPaymentsForRegistration(UUID registrationId)
    {
        return em.createQuery("SELECT p FROM PaymentEntity p WHERE p.registrationId = :registrationId ORDER BY p.transactionDatetime", PaymentEntity.class)
                .setParameter("registrationId", registrationId)
                .getResultList();
    }

    public Set<PaymentEntity> fetchPaymentsForConference(UUID conferenceId)
    {
        throw new NotImplementedException();
    }
}