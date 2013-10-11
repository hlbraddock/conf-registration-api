package org.cru.crs.service;


import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;

import com.google.common.base.Preconditions;


public class PaymentService
{
    EntityManager em;

    @Inject
    public PaymentService(EntityManager em)
    {
        this.em = em;
    }

    public PaymentEntity fetchPaymentBy(UUID id, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
    {
        PaymentEntity payment = em.find(PaymentEntity.class, id);
        if(payment != null)
        {
        	ensureUserHasPermissionsForPayment(crsLoggedInUser, payment);
        }
        
        return payment;
    }

    public void createPaymentRecord(PaymentEntity payment, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
    	ensureUserHasPermissionsForPayment(crsLoggedInUser, payment);
        em.persist(payment);
    }
    
    public void updatePayment(PaymentEntity payment, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
    	ensureUserHasPermissionsForPayment(crsLoggedInUser, payment);
    	em.merge(payment);
    }
    
    public List<PaymentEntity> fetchPaymentsForRegistration(UUID registrationId)
    {
        return em.createQuery("SELECT p FROM PaymentEntity p WHERE p.registrationId = :registrationId ORDER BY p.transactionDatetime", PaymentEntity.class)
                .setParameter("registrationId", registrationId)
                .getResultList();
    }
    
	private void ensureUserHasPermissionsForPayment(CrsApplicationUser crsLoggedInUser, PaymentEntity payment) throws UnauthorizedException
	{
		RegistrationEntity registrationForCurrentPayment = em.find(RegistrationEntity.class, payment.getRegistrationId());
		if(!registrationForCurrentPayment.getUserId().equals(crsLoggedInUser.getId()))
		{
			throw new UnauthorizedException();
		}
	}
}