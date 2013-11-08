package org.cru.crs.api;

import java.math.BigDecimal;
import java.util.UUID;

import org.cru.crs.api.client.PaymentResourceClient;
import org.cru.crs.api.client.RegistrationResourceClient;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.RegistrationFetchProcess;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.sql2o.Sql2o;
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
	AnswerService answerService;
	RegistrationService registrationService;
	
	Sql2o sql;
	
	private UUID registrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");
	private UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");
	private UUID paymentUUID = UUID.fromString("8492F4A8-C7DC-4C0A-BB9E-67E6DCB11111");
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        registrationClient = ProxyFactory.create(RegistrationResourceClient.class, restApiBaseUrl);
        paymentClient = ProxyFactory.create(PaymentResourceClient.class, restApiBaseUrl);
        
        sql = new SqlConnectionProducer().getTestSqlConnection();
		
        paymentService = new PaymentService(sql);
        answerService = new AnswerService(sql);
		registrationService = new RegistrationService(sql,answerService,paymentService);
	}

	@Test(groups="functional-tests")
	public void updatePaymentProcess()
	{
		PaymentEntity processedPayment = null;
		
		Registration registration = RegistrationFetchProcess.buildRegistration(registrationUUID, registrationService, paymentService, answerService);
		
		addCurrentPaymentToRegistration(registration);
		
		try
		{
			ClientResponse updateResponse = paymentClient.updatePayment(registration.getCurrentPayment(), paymentUUID, UserInfo.AuthCode.TestUser);

			Assert.assertEquals(updateResponse.getStatus(), 204);

			processedPayment = paymentService.fetchPaymentBy(registration.getCurrentPayment().getId());

			Assert.assertNotNull(processedPayment.getAuthnetTransactionId());
			Assert.assertNotNull(processedPayment.getTransactionTimestamp());

			ClientResponse<Registration> subsequentResponse = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
			Registration regstrationAfterPayment = subsequentResponse.getEntity();

			Assert.assertNull(regstrationAfterPayment.getCurrentPayment());
			Assert.assertEquals(regstrationAfterPayment.getPastPayments().size(), 1);
			Assert.assertEquals(regstrationAfterPayment.getPastPayments().get(0).getId(), processedPayment.getId());
		}
		finally
		{
			if(processedPayment != null && processedPayment.getId() != null)
			{
				sql.createQuery("DELETE FROM payments WHERE id = :id")
					.addParameter("id", processedPayment.getId())
					.executeUpdate();
			}
		}

	}
		
	private void addCurrentPaymentToRegistration(Registration registration)
	{
		registration.setCurrentPayment(new Payment());
		registration.getCurrentPayment().setId(paymentUUID);
		registration.getCurrentPayment().setRegistrationId(registration.getId());
		registration.getCurrentPayment().setAmount(new BigDecimal(50d));
		registration.getCurrentPayment().setCreditCardExpirationMonth("05");
		registration.getCurrentPayment().setCreditCardExpirationYear("2015");
		registration.getCurrentPayment().setCreditCardNameOnCard("Billy Joe User");
		registration.getCurrentPayment().setCreditCardNumber("4111111111111111");
		registration.getCurrentPayment().setCreditCardCVVNumber("822");
		registration.getCurrentPayment().setReadyToProcess(true);
	}
}
