package org.cru.crs.service;


import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.queries.PaymentQueries;
import org.sql2o.Sql2o;

import com.google.common.base.Preconditions;


public class PaymentService
{
	Sql2o sql;
	
    PaymentQueries paymentQueries;
    
    @Inject
    public PaymentService(Sql2o sql)
    {
    	this.sql = sql;
    	
        this.paymentQueries = new PaymentQueries();
    }

    public PaymentEntity fetchPaymentBy(UUID id)
    {
        return sql.createQuery(paymentQueries.selectById())
        							.addParameter("id", id)
        							.setAutoDeriveColumnNames(true)
        							.executeAndFetchFirst(PaymentEntity.class);
    }

    public void createPaymentRecord(PaymentEntity payment, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
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
    
    public void updatePayment(PaymentEntity payment)
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
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
    
    /**
     * Useful when a registration is going to be deleted, at a minimum we should keep the payment record around.
     * @param paymentId
     */
    public void disassociatePaymentsFromRegistration(UUID registrationId)
    {
    	List<PaymentEntity> payments = fetchPaymentsForRegistration(registrationId);

    	if(payments == null) return;
    	
    	for(PaymentEntity payment : payments)
    	{
    		sql.createQuery(paymentQueries.update())
    				.addParameter("id", payment.getId())
    				.addParameter("registrationId", (UUID)null)
    				.addParameter("authnetTransactionId", payment.getAuthnetTransactionId())
    				.addParameter("ccNameOnCard", payment.getCcNameOnCard())
    				.addParameter("ccExpirationMonth", payment.getCcExpirationMonth())
    				.addParameter("ccExpirationYear", payment.getCcExpirationYear())
    				.addParameter("ccLastFourDigits", payment.getCcLastFourDigits())
    				.addParameter("amount", payment.getAmount())
    				.addParameter("transactionTimestamp", payment.getTransactionTimestamp())
    				.executeUpdate();
    	}
    }
}