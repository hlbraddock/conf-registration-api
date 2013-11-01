package org.cru.crs.service;


import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.cru.crs.model.queries.PaymentQueries;
import org.sql2o.Sql2o;

import com.google.common.base.Preconditions;


public class PaymentService
{
	Sql2o sql;

	RegistrationService registrationService;
	
    PaymentQueries paymentQueries;
    
    @Inject
    public PaymentService(Sql2o sql, RegistrationService registrationService)
    {
    	this.sql = sql;
    	this.sql.setDefaultColumnMappings(EntityColumnMappings.get(PaymentEntity.class));
    	this.registrationService = registrationService;
    	
        this.paymentQueries = new PaymentQueries();
    }

    public PaymentEntity fetchPaymentBy(UUID id, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
    {
        PaymentEntity payment = sql.createQuery(paymentQueries.selectById())
        							.addParameter("id", id)
        							.setAutoDeriveColumnNames(true)
        							.executeAndFetchFirst(PaymentEntity.class);
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
        sql.createQuery(paymentQueries.insert())
        		.addParameter("id", payment.getId())
        		.addParameter("registrationId", payment.getRegistrationId())
        		.addParameter("authnetTransactionId", payment.getAuthnetTransactionId())
        		.addParameter("ccNameOnCard", payment.getCcNameOnCard())
        		.addParameter("ccExpirationMonth", payment.getCcExpirationMonth())
        		.addParameter("ccExpirationYear", payment.getCcExpirationYear())
        		.addParameter("ccLastFourDigits", payment.getCcLastFourDigits())
        		.addParameter("amount", payment.getAmount())
        		.addParameter("transactionTimestamp", payment.getTransactionTimestamp())
        		.executeUpdate();
    }
    
    public void updatePayment(PaymentEntity payment, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
    	ensureUserHasPermissionsForPayment(crsLoggedInUser, payment);
    	sql.createQuery(paymentQueries.update())
				.addParameter("id", payment.getId())
				.addParameter("registrationId", payment.getRegistrationId())
				.addParameter("authnetTransactionId", payment.getAuthnetTransactionId())
				.addParameter("ccNameOnCard", payment.getCcNameOnCard())
				.addParameter("ccExpirationMonth", payment.getCcExpirationMonth())
				.addParameter("ccExpirationYear", payment.getCcExpirationYear())
				.addParameter("ccLastFourDigits", payment.getCcLastFourDigits())
				.addParameter("amount", payment.getAmount())
				.addParameter("transactionTimestamp", payment.getTransactionTimestamp())
				.executeUpdate();
    }
    
    public List<PaymentEntity> fetchPaymentsForRegistration(UUID registrationId)
    {
        return sql.createQuery(paymentQueries.selectAllForRegistration())
        			.addParameter("registrationId", registrationId)
        			.setAutoDeriveColumnNames(true)
        			.executeAndFetch(PaymentEntity.class);
    }
    
	private void ensureUserHasPermissionsForPayment(CrsApplicationUser crsLoggedInUser, PaymentEntity payment) throws UnauthorizedException
	{
		RegistrationEntity registrationForCurrentPayment = registrationService.getRegistrationBy(payment.getRegistrationId(), crsLoggedInUser);
		if(!registrationForCurrentPayment.getUserId().equals(crsLoggedInUser.getId()))
		{
			throw new UnauthorizedException();
		}
	}
}