package org.cru.crs.payment.authnet;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.payment.authnet.model.CreditCard;
import org.cru.crs.payment.authnet.model.GatewayConfiguration;
import org.cru.crs.payment.authnet.model.Invoice;
import org.cru.crs.payment.authnet.model.Merchant;
import org.cru.crs.payment.authnet.transaction.AuthCapture;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;
import org.joda.time.DateTime;

public class AuthnetPaymentProcess
{
	private CrsProperties crsProperties;
	private HttpProvider httpProvider;
	
	@Inject
	public AuthnetPaymentProcess(CrsProperties props, HttpProvider httpProvider)
	{
		this.crsProperties = props;
		this.httpProvider = httpProvider;
	}
	
	public Long processCreditCardTransaction(Conference conference, Registration registration, Payment payment) throws IOException
	{
		AuthCapture authnetAuthorizeCapture = new AuthCapture();
		
		authnetAuthorizeCapture.setAmount(payment.getAmount());
		authnetAuthorizeCapture.setCreditCard(createCreditCard(payment));
		authnetAuthorizeCapture.setCurrency(crsProperties.getProperty("currencyCode"));
		/*this isn't required by the authnet api, which is convenient because
		 * we don't have an easy way to parse it out of our questions*/
		authnetAuthorizeCapture.setCustomer(null);
		authnetAuthorizeCapture.setGatewayConfiguration(createGatewayConfiguration());
		authnetAuthorizeCapture.setHttpProvider(httpProvider);
		authnetAuthorizeCapture.setInvoice(createInvoice(conference,payment));
		authnetAuthorizeCapture.setLog(Logger.getLogger(this.getClass()));
		authnetAuthorizeCapture.setMerchant(createMerchant(conference));
		authnetAuthorizeCapture.setMethod(new CreditCardMethod());
		authnetAuthorizeCapture.setTransactionResult(null);
		authnetAuthorizeCapture.setUrl(crsProperties.getProperty("authnetTestApiUrl"));
		
		authnetAuthorizeCapture.execute();
		
		//eventually this will be the authnet transaction id, but for now just return something useful;
		return authnetAuthorizeCapture.getTransactionResult().getTransactionID();
		
	}

	CreditCard createCreditCard(Payment payment)
	{
		CreditCard creditCard = new CreditCard();
		
		creditCard.setCardNumber(payment.getCreditCardNumber());
		creditCard.setExpirationDate(new DateTime().withYear(Integer.parseInt(payment.getCreditCardExpirationYear()))
												   .withMonthOfYear(Integer.parseInt(payment.getCreditCardExpirationMonth()))
												   .dayOfMonth().withMaximumValue()
												   .secondOfDay().withMaximumValue()
												   .toDate());
		
		creditCard.setCardCode(payment.getCreditCardCVVNumber());
		
		
		return creditCard;
	}
	
	GatewayConfiguration createGatewayConfiguration()
	{
		return new GatewayConfiguration().setTestRequest(Boolean.valueOf(crsProperties.getProperty("authnetTestMode")));
	}
	
	Invoice createInvoice(Conference conference, Payment payment)
	{
		Invoice invoice = new Invoice();
		
		invoice.setDescription(conference.getName());
		invoice.setInvoiceNum(payment.getId().toString());
		return invoice;
	}
	
	Merchant createMerchant(Conference conference)
	{
		Merchant merchant = new Merchant();
		
		merchant.setEmail(conference.getContactPersonEmail());
		merchant.setLogin(conference.getAuthnetId());
		merchant.setTranKey(conference.getAuthnetToken());
		return merchant;
	}
}
