package org.cru.crs.payment.authnet;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.utils.CrsProperties;
import org.joda.time.DateTime;

public class AuthnetPaymentProcess
{
	
	@Inject CrsProperties crsProperties;
	
	public void processCreditCardTransaction(Conference conference, Registration registration, Payment payment) throws IOException
	{
		AuthCapture authnetAuthorizeCapture = new AuthCapture();
		
		authnetAuthorizeCapture.setAmount(payment.getAmount());
		authnetAuthorizeCapture.setCreditCard(createCreditCard(payment));
		authnetAuthorizeCapture.setCurrency(crsProperties.getProperty("currencyCode"));
		authnetAuthorizeCapture.setCustomer(createCustomer());
		authnetAuthorizeCapture.setGatewayConfiguration(createGatewayConfiguration());
		authnetAuthorizeCapture.setHttpProvider(null);
		authnetAuthorizeCapture.setInvoice(createInvoice(conference,payment));
		authnetAuthorizeCapture.setLog(Logger.getLogger(this.getClass()));
		authnetAuthorizeCapture.setMerchant(createMerchant(conference));
		authnetAuthorizeCapture.setMethod(new CreditCardMethod());
		authnetAuthorizeCapture.setTransactionResult(null);
		authnetAuthorizeCapture.setUrl(crsProperties.getProperty("authnetApiUrl"));
		
		authnetAuthorizeCapture.execute();
	}

	private CreditCard createCreditCard(Payment payment)
	{
		CreditCard creditCard = new CreditCard();
		
		creditCard.setCardNumber(payment.getCreditCardNumber());
		creditCard.setExpirationDate(new DateTime().withYear(Integer.parseInt(payment.getCreditCardExpirationYear()))
												   .withMonthOfYear(Integer.parseInt(payment.getCreditCardExpirationMonth()))
												   .dayOfMonth().withMaximumValue()
												   .secondOfDay().withMaximumValue()
												   .toDate());
		
		return creditCard;
	}
	
	private Customer createCustomer()
	{
		Customer customer = new Customer();
		
		//TODO: figure out how to get this from the registration
		return customer;
	}
	
	private GatewayConfiguration createGatewayConfiguration()
	{
		return new GatewayConfiguration().setTestRequest(Boolean.valueOf(crsProperties.getProperty("authnetTestMode")));
	}
	
	private Invoice createInvoice(Conference conference, Payment payment)
	{
		Invoice invoice = new Invoice();
		
		invoice.setDescription(conference.getName());
		invoice.setInvoiceNum(payment.getId().toString());
		return invoice;
	}
	
	private Merchant createMerchant(Conference conference)
	{
		Merchant merchant = new Merchant();
		
		merchant.setEmail(conference.getContactPersonEmail());
		merchant.setLogin(conference.getAuthnetId());
		merchant.setTranKey(conference.getAuthnetToken());
		return merchant;
	}
}
