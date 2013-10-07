package org.cru.crs.payment.authnet;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.payment.authnet.model.CreditCard;
import org.cru.crs.payment.authnet.model.Invoice;
import org.cru.crs.payment.authnet.model.Merchant;
import org.cru.crs.utils.AuthCodeGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthnetPaymentProcessTest
{

	@Test(groups="unittest")
	public void createCreditCard()
	{
		//properties are not needed for this test
		AuthnetPaymentProcess paymentProcess = new AuthnetPaymentProcess(null,new HttpClientProviderImpl(new HttpClient()));
		
		CreditCard creditCard = paymentProcess.createCreditCard(testPaymentOne());
		
		Assert.assertNotNull(creditCard);
		
		Map<String, String> creditCardParams = creditCard.getParamMap();
		
		Assert.assertNotNull(creditCardParams);
		Assert.assertFalse(creditCardParams.isEmpty());
		Assert.assertEquals(creditCard.getCardNumber(), "4111111111111111");
		Assert.assertEquals(creditCardParams.get("x_card_num"), "4111111111111111");
		Assert.assertEquals(creditCard.getFormattedExpirationDate(), "05/2015");
		Assert.assertEquals(creditCardParams.get("x_exp_date"), "05/2015");
		Assert.assertNull(creditCard.getCardCode());
		Assert.assertNull(creditCardParams.get("x_card_code"));
	}
	
	@Test(groups="unittest")
	public void createInvoice()
	{
		//properties are not needed for this test
		AuthnetPaymentProcess paymentProcess = new AuthnetPaymentProcess(null,new HttpClientProviderImpl(new HttpClient()));
		Conference testConference = testConferenceOne();
		Payment testPayment = testPaymentOne();
		
		Invoice invoice = paymentProcess.createInvoice(testConference, testPayment);
		
		Assert.assertNotNull(invoice);
		
		Map<String, String> invoiceParams = invoice.getParamMap();
		
		Assert.assertNotNull(invoiceParams);
		Assert.assertFalse(invoiceParams.isEmpty());
		Assert.assertEquals(invoice.getInvoiceNum(), testPayment.getId().toString());
		Assert.assertEquals(invoiceParams.get("x_invoice_num"), testPayment.getId().toString());
		Assert.assertEquals(invoice.getDescription(), "Super Duper Fall Retreat");
		Assert.assertEquals(invoiceParams.get("x_description"), "Super Duper Fall Retreat");

	}
	
	@Test(groups="unittest")
	public void createMerchant()
	{
		//properties are not needed for this test
		AuthnetPaymentProcess paymentProcess = new AuthnetPaymentProcess(null,new HttpClientProviderImpl(new HttpClient()));
		Conference testConference = testConferenceOne();
			
		Merchant merchant = paymentProcess.createMerchant(testConference);
		
		Assert.assertNotNull(merchant);
		
		Map<String, String> merchantParams = merchant.getParamMap();
		
		Assert.assertNotNull(merchantParams);
		Assert.assertFalse(merchantParams.isEmpty());
		Assert.assertEquals(merchant.getLogin(), testConference.getAuthnetId());
		Assert.assertEquals(merchantParams.get("x_login"), testConference.getAuthnetId());
		Assert.assertEquals(merchant.getTranKey(), testConference.getAuthnetToken().toString());
		Assert.assertEquals(merchantParams.get("x_tran_key"), testConference.getAuthnetToken().toString());
		Assert.assertEquals(merchant.getEmail(), "joe.user@cru.org");
		Assert.assertEquals(merchantParams.get("x_email_merchant"), "joe.user@cru.org");
		
	}

	private Payment testPaymentOne()
	{
		Payment testPayment = new Payment();
		
		testPayment.setId(UUID.randomUUID());
		testPayment.setAmount(new BigDecimal(50.00f));
		testPayment.setCreditCardExpirationMonth("05");
		testPayment.setCreditCardExpirationYear("2015");
		testPayment.setCreditCardNumber("4111111111111111");
		testPayment.setCreditCardNameOnCard("Joe User");
		testPayment.setRegistrationId(UUID.randomUUID());
		
		return testPayment;
	}
	
	private Conference testConferenceOne()
	{
		Conference testConference = new Conference();
		
		testConference.setAcceptCreditCards(true);
		testConference.setAuthnetId(AuthCodeGenerator.generate());
		testConference.setAuthnetToken(UUID.randomUUID().toString());
		testConference.setConferenceCost(new BigDecimal(55.00f));
		testConference.setName("Super Duper Fall Retreat");
		testConference.setContactPersonEmail("joe.user@cru.org");
		
		return testConference;
	}
}
