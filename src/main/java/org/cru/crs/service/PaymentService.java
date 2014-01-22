package org.cru.crs.service;


import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.ccci.util.NotImplementedException;
import org.cru.crs.model.PaymentEntity;
import org.sql2o.Connection;

import com.google.common.base.Preconditions;

@RequestScoped
public class PaymentService
{
	org.sql2o.Connection sqlConnection;
	    
	/*Weld requires a default no args constructor to proxy this object*/
    public PaymentService(){ }
    
    @Inject
    public PaymentService(Connection sqlConnection)
    {
    	this.sqlConnection = sqlConnection;
    }

    public PaymentEntity fetchPaymentBy(UUID id)
    {
        return sqlConnection.createQuery(PaymentQueries.selectById())
        							.addParameter("id", id)
        							.setAutoDeriveColumnNames(true)
        							.executeAndFetchFirst(PaymentEntity.class);
    }

    public void createPaymentRecord(PaymentEntity payment)
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
    	sqlConnection.createQuery(PaymentQueries.insert())
        				.addParameter("id", payment.getId())
        				.addParameter("registrationId", payment.getRegistrationId())
        				.addParameter("authnetTransactionId", payment.getAuthnetTransactionId())
        				.addParameter("ccNameOnCard", payment.getCcNameOnCard())
        				.addParameter("ccExpirationMonth", payment.getCcExpirationMonth())
        				.addParameter("ccExpirationYear", payment.getCcExpirationYear())
        				.addParameter("ccLastFourDigits", payment.getCcLastFourDigits())
        				.addParameter("amount", payment.getAmount())
        				.addParameter("transactionTimestamp", payment.getTransactionTimestamp())
        				.addParameter("paymentType", (Object)payment.getPaymentType())
        				.executeUpdate();
    }
    
    public void updatePayment(PaymentEntity payment)
    {
    	Preconditions.checkNotNull(payment.getRegistrationId());
    	sqlConnection.createQuery(PaymentQueries.update())
    					.addParameter("id", payment.getId())
    					.addParameter("registrationId", payment.getRegistrationId())
    					.addParameter("authnetTransactionId", payment.getAuthnetTransactionId())
    					.addParameter("ccNameOnCard", payment.getCcNameOnCard())
    					.addParameter("ccExpirationMonth", payment.getCcExpirationMonth())
    					.addParameter("ccExpirationYear", payment.getCcExpirationYear())
    					.addParameter("ccLastFourDigits", payment.getCcLastFourDigits())
    					.addParameter("amount", payment.getAmount())
    					.addParameter("transactionTimestamp", payment.getTransactionTimestamp())
    					.addParameter("paymentType", (Object)payment.getPaymentType())
    					.executeUpdate();
    }
    
    public List<PaymentEntity> fetchPaymentsForRegistration(UUID registrationId)
    {
        return sqlConnection.createQuery(PaymentQueries.selectAllForRegistration())
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
    		sqlConnection.createQuery(PaymentQueries.update())
    						.addParameter("id", payment.getId())
    						.addParameter("registrationId", (UUID)null)
    						.addParameter("authnetTransactionId", payment.getAuthnetTransactionId())
    						.addParameter("ccNameOnCard", payment.getCcNameOnCard())
    						.addParameter("ccExpirationMonth", payment.getCcExpirationMonth())
    						.addParameter("ccExpirationYear", payment.getCcExpirationYear())
    						.addParameter("ccLastFourDigits", payment.getCcLastFourDigits())
    						.addParameter("amount", payment.getAmount())
    						.addParameter("transactionTimestamp", payment.getTransactionTimestamp())
    						.addParameter("paymentType", (Object)payment.getPaymentType())
    						.executeUpdate();
    	}
    }
    
    private static class PaymentQueries
    {
    	private static  String selectById()
    	{
    		return "SELECT * FROM payments WHERE id = :id";
    	}
    	
    	private static  String selectAllForRegistration()
    	{
    		return "SELECT * FROM payments WHERE registration_id = :registrationId";
    	}
    	
    	private static  String update()
    	{
    		return "UPDATE payments SET " +
    				 "registration_id = :registrationId, " +
    				 "authnet_transaction_id = :authnetTransactionId, " +
    				 "cc_name_on_card = :ccNameOnCard, " +
    				 "cc_expiration_month = :ccExpirationMonth, " +
    				 "cc_expiration_year = :ccExpirationYear, " +
    				 "cc_last_four_digits = :ccLastFourDigits, " +
    				 "amount = :amount, " +
    				 "transaction_timestamp = :transactionTimestamp, " +
    				 "payment_type = :paymentType" +
    				 " WHERE  " +
    				 "id = :id";
    	}

    	private static  String insert()
    	{
    		return "INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp, payment_type) " + 
    			   "VALUES (:id, :registrationId, :authnetTransactionId, :ccNameOnCard, :ccExpirationMonth, :ccExpirationYear, :ccLastFourDigits, :amount, :transactionTimestamp, :paymentType)";
    	}

    	private static String delete()
    	{
    		throw new NotImplementedException();
    	}
    }
}