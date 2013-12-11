package org.cru.crs.service;


import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.queries.PaymentQueries;
import org.sql2o.Connection;

import com.google.common.base.Preconditions;

@RequestScoped
public class PaymentService
{
	org.sql2o.Connection sqlConnection;
	
    PaymentQueries paymentQueries;
    
    /*required for Weld*/
    public PaymentService(){ }
    
    @Inject
    public PaymentService(Connection sqlConnection)
    {
    	this.sqlConnection = sqlConnection;
    	
        this.paymentQueries = new PaymentQueries();
    }

    public PaymentEntity fetchPaymentBy(UUID id)
    {
        return sqlConnection.createQuery(paymentQueries.selectById())
        							.addParameter("id", id)
        							.setAutoDeriveColumnNames(true)
        							.executeAndFetchFirst(PaymentEntity.class);
    }

    public void createPaymentRecord(PaymentEntity payment)
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
    	sqlConnection.createQuery(paymentQueries.insert())
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
    	sqlConnection.createQuery(paymentQueries.update())
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
        return sqlConnection.createQuery(paymentQueries.selectAllForRegistration())
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
    		sqlConnection.createQuery(paymentQueries.update())
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