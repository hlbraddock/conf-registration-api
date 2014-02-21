package org.cru.crs.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.cru.crs.api.client.PaymentResourceClient;
import org.cru.crs.api.client.RegistrationResourceClient;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.RetrieveRegistrationProcess;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.cru.crs.service.PaymentService;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PaymentResourceFunctionalTest
{

	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;
	RegistrationResourceClient registrationClient;
	PaymentResourceClient paymentClient;
	
	PaymentService paymentService;
	
	RetrieveRegistrationProcess registrationFetchProcess;
	
	Connection sqlConnection;
	
	private UUID registrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");
	private UUID paymentUUID = UUID.fromString("8492F4A8-C7DC-4C0A-BB9E-67E6DCB11111");
	private UUID refundedPaymentId = UUID.fromString("8492F4A8-C7DC-4C0A-BB9E-67E6DCB22222");
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        registrationClient = ProxyFactory.create(RegistrationResourceClient.class, restApiBaseUrl);
        paymentClient = ProxyFactory.create(PaymentResourceClient.class, restApiBaseUrl);
        
        sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
        paymentService = ServiceFactory.createPaymentService(sqlConnection);
		
		registrationFetchProcess = new RetrieveRegistrationProcess(ServiceFactory.createRegistrationService(sqlConnection),
																	paymentService,
																	ServiceFactory.createAnswerService(sqlConnection));
	}

	@Test(groups="functional-tests")
	public void updatePaymentProcess()
	{
		PaymentEntity processedPayment = null;
		
		Registration registration = registrationFetchProcess.get(registrationUUID);
		
		Payment payment = addCurrentPaymentToRegistration(registrationUUID);
		
		try
		{
			ClientResponse updateResponse = paymentClient.updatePayment(payment, paymentUUID, UserInfo.AuthCode.TestUser);

			Assert.assertEquals(updateResponse.getStatus(), 204);

			processedPayment = paymentService.getPaymentById(payment.getId());

			Assert.assertNotNull(processedPayment.getAuthnetTransactionId());
			Assert.assertNotNull(processedPayment.getTransactionTimestamp());

			ClientResponse<Registration> subsequentResponse = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
			Registration regstrationAfterPayment = subsequentResponse.getEntity();

			Assert.assertEquals(regstrationAfterPayment.getPastPayments().size(), 1);
			Assert.assertEquals(regstrationAfterPayment.getPastPayments().get(0).getId(), processedPayment.getId());
		}
		finally
		{
			if(processedPayment != null && processedPayment.getId() != null)
			{
				sqlConnection.createQuery("DELETE FROM payments WHERE id = :id")
									.addParameter("id", processedPayment.getId())
									.executeUpdate();

				sqlConnection.commit();
			}
		}

	}
	
	/**
	 * While this test is a functional test, it shouldn't always be run.  It depends upon specific conditions to be true in
	 * our authorize.net test account.  It needs for there to be a transaction posted and settled, but not more than 120 days
	 * old.  A transaction takes about a day to settle in the test environment.
	 * 
	 * So, to run this test:
	 *  - set authnetTestMode = false in /apps/apps-config/crs-conf-api-properties.xml (it still is posting to the TEST environment, don't worry!)
	 *  - run a functional/unit test that posts a new payment to authorize.net
	 *  - grab the transaction id from that successful payment and set it as the authnet transaction ID for payment8492F4A8-C7DC-4C0A-BB9E-67E6DCB22222 in payments.sql
	 *  - update your local database
	 *  - wait one day until the transaction settles
	 *  - run this test
	 *  
	 *  If anyone finds a better process, feel free to post it :)
	 */
	public void refundFullPayment()
	{
		Payment refund = createRefund(registrationUUID, new BigDecimal("20.00"));
		
		try
		{
			ClientResponse response = paymentClient.createPayment(refund,  UserInfo.AuthCode.TestUser);
			
			Assert.assertEquals(response.getStatus(), 201);
			
			List<PaymentEntity> paymentsForRegistration = paymentService.getPaymentsForRegistration(registrationUUID);
			
			for(PaymentEntity payment : paymentsForRegistration)
			{
				if(payment.getId().equals(refund.getId()))
				{
					Assert.assertEquals(payment.getPaymentType(), PaymentType.CREDIT_CARD_REFUND);
					Assert.assertEquals(payment.getAmount(), new BigDecimal("20.00"));
					Assert.assertEquals(payment.getRefundedPaymentId(), refundedPaymentId);
				}
			}
		}
		
		finally
		{
			sqlConnection.createQuery("DELETE FROM payments WHERE id = :id")
			.addParameter("id", refund.getId())
			.executeUpdate();
			
			sqlConnection.commit();
		}
	}
	
	private Payment createRefund(UUID registrationId, BigDecimal amount)
	{
		Payment refund = new Payment();
		
		refund.setId(UUID.randomUUID());
		refund.setRegistrationId(registrationId);
        refund.setCreditCard(new Payment.CreditCardPayment());
		refund.getCreditCard().setLastFourDigits("1111");
		refund.setAmount(amount);
		refund.setPaymentType(PaymentType.CREDIT_CARD_REFUND);
		refund.setRefundedPaymentId(refundedPaymentId);
		refund.setReadyToProcess(true);
		
		return refund;
	}
	
	private Payment addCurrentPaymentToRegistration(UUID registrationUUID)
	{
		Payment payment = new Payment();
		payment.setId(paymentUUID);
		payment.setRegistrationId(registrationUUID);
		payment.setAmount(new BigDecimal(50d));
        payment.setCreditCard(new Payment.CreditCardPayment());
		payment.getCreditCard().setExpirationMonth("05");
		payment.getCreditCard().setExpirationYear("2015");
		payment.getCreditCard().setNameOnCard("Billy Joe User");
		payment.getCreditCard().setNumber("4111111111111111");
		payment.getCreditCard().setCvvNumber("822");
		payment.setReadyToProcess(true);
		payment.setPaymentType(PaymentType.CREDIT_CARD);

		return payment;
	}
}
