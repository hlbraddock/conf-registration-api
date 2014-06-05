package org.cru.crs.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PaymentServiceTest extends AbstractTestWithDatabaseConnectivity
{
	PaymentService paymentService;

	@BeforeMethod(alwaysRun=true)
	private void setupConnectionAndService()
	{
		refreshConnection();
		paymentService = new PaymentService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetPayment()
	{
		PaymentEntity payment = paymentService.getPaymentById(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222"));
		
		Assert.assertNotNull(payment);
		
		Assert.assertEquals(payment.getId(), UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222"));
		Assert.assertEquals(payment.getCcExpirationMonth(), "04");
		Assert.assertEquals(payment.getCcExpirationYear(), "2014");
		Assert.assertEquals(payment.getCcLastFourDigits(), "1111");
		Assert.assertEquals(payment.getAmount(), new BigDecimal("20.00"));
		Assert.assertEquals(payment.getAuthnetTransactionId(), "2205342461");
		Assert.assertEquals(payment.getRegistrationId(), UUID.fromString("aaaaf4a8-c7dc-4c0a-bb9e-67e6dcb91111"));
		Assert.assertEquals(payment.getTransactionTimestamp(), DateTimeCreaterHelper.createDateTime(2013, 8, 21, 19, 22, 7));
		Assert.assertEquals(payment.getPaymentType(), PaymentType.CREDIT_CARD);
	}
	
	@Test(groups="dbtest")
	public void testSavePayment()
	{
		UUID id = UUID.randomUUID();
		
		PaymentEntity newPayment = new PaymentEntity();
		
		newPayment.setId(id);
		newPayment.setRegistrationId(UUID.fromString("b2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));
		newPayment.setAmount(new BigDecimal("35.00"));
		newPayment.setAuthnetTransactionId("1");
		newPayment.setCcExpirationMonth("05");
		newPayment.setCcExpirationYear("2022");
		newPayment.setCcLastFourDigits("1234");
		newPayment.setCcNameOnCard("Hank Williamson");
		newPayment.setTransactionTimestamp(DateTimeCreaterHelper.createDateTime(2015, 9, 30, 15, 11, 51));
		newPayment.setPaymentType(PaymentType.CHECK);
        newPayment.setCheckNumber("1234");
		
		try
		{
			paymentService.createPaymentRecord(newPayment, UserInfo.Users.TestUser);
			
			PaymentEntity retrievedPayment = paymentService.getPaymentById(id);
			
			Assert.assertNotNull(retrievedPayment);
			Assert.assertEquals(retrievedPayment.getId(),id);
			Assert.assertEquals(retrievedPayment.getRegistrationId(), UUID.fromString("b2bff4a8-c7dc-4c0a-bb9e-67e6dcb982e7"));
			Assert.assertEquals(retrievedPayment.getAmount(), new BigDecimal("35.00"));
			Assert.assertEquals(retrievedPayment.getAuthnetTransactionId(), "1");
			Assert.assertEquals(retrievedPayment.getCcExpirationMonth(), "05");
			Assert.assertEquals(retrievedPayment.getCcExpirationYear(), "2022");
			Assert.assertEquals(retrievedPayment.getCcLastFourDigits(), "1234");
			Assert.assertEquals(retrievedPayment.getCcNameOnCard(), "Hank Williamson");
			Assert.assertEquals(retrievedPayment.getTransactionTimestamp(), DateTimeCreaterHelper.createDateTime(2015, 9, 30, 15, 11, 51));
			Assert.assertEquals(retrievedPayment.getPaymentType(), PaymentType.CHECK);
            Assert.assertEquals(retrievedPayment.getCheckNumber(), "1234");
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest")
	public void testUpdatePayment()
	{		
		PaymentEntity paymentToUpdate = new PaymentEntity();
		
		paymentToUpdate.setId(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222"));
		paymentToUpdate.setAmount(new BigDecimal("22.25"));
		paymentToUpdate.setAuthnetTransactionId("2");
		paymentToUpdate.setCcExpirationMonth("08");
		paymentToUpdate.setCcExpirationYear("2015");
		paymentToUpdate.setCcLastFourDigits("1252");
		paymentToUpdate.setCcNameOnCard("Ryan C");
		paymentToUpdate.setRegistrationId(UUID.fromString("aaaaf4a8-c7dc-4c0a-bb9e-67e6dcb91111"));
		paymentToUpdate.setTransactionTimestamp(DateTimeCreaterHelper.createDateTime(2015, 9, 30, 15, 11, 51));
		paymentToUpdate.setPaymentType(PaymentType.TRANSFER);
        paymentToUpdate.setTransferSource("/todd/gross/chartfield");
        paymentToUpdate.setDescription("Thanks, Todd!");
		
		try
		{
			paymentService.updatePayment(paymentToUpdate, UserInfo.Users.TestUser);
			
			PaymentEntity retrievedPayment = paymentService.getPaymentById(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222"));
			
			Assert.assertNotNull(retrievedPayment);
			Assert.assertEquals(retrievedPayment.getId(),UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222"));
			Assert.assertEquals(retrievedPayment.getRegistrationId(), UUID.fromString("aaaaf4a8-c7dc-4c0a-bb9e-67e6dcb91111"));
			Assert.assertEquals(retrievedPayment.getAmount(), new BigDecimal("22.25"));
			Assert.assertEquals(retrievedPayment.getAuthnetTransactionId(), "2");
			Assert.assertEquals(retrievedPayment.getCcExpirationMonth(), "08");
			Assert.assertEquals(retrievedPayment.getCcExpirationYear(), "2015");
			Assert.assertEquals(retrievedPayment.getCcLastFourDigits(), "1252");
			Assert.assertEquals(retrievedPayment.getCcNameOnCard(), "Ryan C");
			Assert.assertEquals(retrievedPayment.getTransactionTimestamp(), DateTimeCreaterHelper.createDateTime(2015, 9, 30, 15, 11, 51));
			Assert.assertEquals(retrievedPayment.getPaymentType(), PaymentType.TRANSFER);
            Assert.assertEquals(retrievedPayment.getTransferSource(), "/todd/gross/chartfield");
            Assert.assertEquals(retrievedPayment.getDescription(), "Thanks, Todd!");
		}
		finally
		{
			sqlConnection.rollback();
		}
		
	}
	
	@Test(groups="dbtest")
	public void testGetPaymentsForRegistration()
	{
		List<PaymentEntity> payments = paymentService.getPaymentsForRegistration(UUID.fromString("aaaaf4a8-c7dc-4c0a-bb9e-67e6dcb91111"));
		
		Assert.assertNotNull(payments);
		Assert.assertEquals(payments.size(), 2);
		
		for(PaymentEntity payment : payments)
		{
			if(!payment.getId().equals(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb33333")) && 
				!payment.getId().equals(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222")))
			{
				Assert.fail("Wrong payment was associated to this registration");
			}
		}
	}
	
	@Test(groups="dbtest")
	public void testDisassociatePaymentsFromRegistration()
	{
		try
		{
			paymentService.disassociatePaymentsFromRegistration(UUID.fromString("aaaaf4a8-c7dc-4c0a-bb9e-67e6dcb91111"), UserInfo.Users.TestUser);
			
			Assert.assertTrue(paymentService.getPaymentsForRegistration(UUID.fromString("aaaaf4a8-c7dc-4c0a-bb9e-67e6dcb91111")).isEmpty());
			Assert.assertNotNull(paymentService.getPaymentById(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb22222")));
			Assert.assertNotNull(paymentService.getPaymentById(UUID.fromString("8492f4a8-c7dc-4c0a-bb9e-67e6dcb33333")));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
}
