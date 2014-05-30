package org.cru.crs.payment.trustcommerce;

import org.cru.crs.api.model.Payment;
import org.cru.crs.model.PaymentType;
import org.cru.crs.payment.trustcommerce.domain.Action;
import org.cru.crs.payment.trustcommerce.domain.Response;
import org.cru.crs.payment.trustcommerce.domain.TrustCommerceException;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class TrustCommercePaymentProcessTest
{
	CrsProperties crsProperties = new CrsPropertiesFactory().get();

	String cardNumber = "4111111111111111";
	String cardCode = "123";
	String expMonth = "05";
	String expYear = "2015";
	String name = "John James";
	BigDecimal amount = new BigDecimal(50.00f);

	@Test(groups="unittest")
	public void processCreditCardTransaction() throws TrustCommerceException
	{
		TrustCommercePaymentProcess trustCommercePaymentProcess = new TrustCommercePaymentProcess(crsProperties);

		Payment payment = getPayment(UUID.randomUUID(), cardNumber, cardCode, expMonth, expYear, name, amount);

		// throws exception on failure
		Map<String,String> response = trustCommercePaymentProcess.processCreditCardTransaction(payment, Action.SALE);

		System.out.println(response);

		String transactionId = response.get(Response.TRANSID.getValue());

		Assert.assertNotNull(transactionId);
	}

	private Payment getPayment(UUID registrationUUID, String cardNumber, String cardCode, String expMonth, String expYear, String name, BigDecimal amount)
	{
		Payment payment = new Payment();

		payment.setId(UUID.randomUUID());
		payment.setRegistrationId(registrationUUID);
		payment.setAmount(amount);
		payment.setPaymentType(PaymentType.CREDIT_CARD);
		payment.setCreditCard(new Payment.CreditCardPayment());
		payment.getCreditCard().setExpirationMonth(expMonth);
		payment.getCreditCard().setExpirationYear(expYear);
		payment.getCreditCard().setNumber(cardNumber);
		payment.getCreditCard().setCvvNumber(cardCode);
		payment.getCreditCard().setNameOnCard(name);
		payment.setRegistrationId(UUID.randomUUID());

		return payment;
	}
}
