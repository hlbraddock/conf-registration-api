package org.cru.crs.payment.authnet;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.payment.authnet.model.CreditCard;
import org.cru.crs.payment.authnet.model.GatewayConfiguration;
import org.cru.crs.payment.authnet.model.Invoice;
import org.cru.crs.payment.authnet.model.Merchant;
import org.cru.crs.payment.authnet.transaction.AuthCapture;
import org.cru.crs.payment.authnet.transaction.Credit;
import org.cru.crs.payment.authnet.transaction.TransactionResult;
import org.cru.crs.payment.authnet.transaction.Void;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.utils.CrsProperties;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import java.io.IOException;

@Authnet
public class AuthnetPaymentProcess
{
	private CrsProperties crsProperties;
	private HttpProvider httpProvider;
	private ConferenceCostsService conferenceCostsService;

	@Inject
	public AuthnetPaymentProcess(CrsProperties props, HttpProvider httpProvider, ConferenceCostsService conferenceCostsService)
	{
		this.crsProperties = props;
		this.httpProvider = httpProvider;
		this.conferenceCostsService = conferenceCostsService;
	}
	
	public String processCreditCardTransaction(Conference conference, Payment payment) throws IOException
	{
		AuthCapture authnetAuthorizeCapture = new AuthCapture();
		
		authnetAuthorizeCapture.setAmount(payment.getAmount());
		authnetAuthorizeCapture.setCreditCard(createCreditCard(payment));
		authnetAuthorizeCapture.setCurrency(crsProperties.getProperty("currencyCode"));
		/* this isn't required by the authnet api, which is convenient because
		 * we don't have an easy way to parse it out of our questions*/
		authnetAuthorizeCapture.setCustomer(null);
		authnetAuthorizeCapture.setGatewayConfiguration(createGatewayConfiguration());
		authnetAuthorizeCapture.setHttpProvider(httpProvider);
		authnetAuthorizeCapture.setInvoice(createInvoice(conference,payment));
		authnetAuthorizeCapture.setLog(Logger.getLogger(this.getClass()));
		authnetAuthorizeCapture.setMerchant(createMerchant(conference));
		authnetAuthorizeCapture.setMethod(new CreditCardMethod());
		authnetAuthorizeCapture.setTransactionResult(null);
		authnetAuthorizeCapture.setUrl(crsProperties.getProperty("authnetUrl"));
		
		authnetAuthorizeCapture.execute();
		
		//eventually this will be the authnet transaction id, but for now just return something useful;
		TransactionResult transactionResult = authnetAuthorizeCapture.getTransactionResult();
		return transactionResult.getTransactionID();
		
	}

	public String processCreditCardRefund(Conference conference, Payment payment) throws IOException
	{
		Credit authnetCredit = new Credit();
		
		authnetCredit.setAmount(payment.getAmount());
		authnetCredit.setCreditCard(createCreditCardLastFourDigitsOnly(payment));
		authnetCredit.setGatewayConfiguration(createGatewayConfiguration());
		authnetCredit.setHttpProvider(httpProvider);
		authnetCredit.setLog(Logger.getLogger(this.getClass()));
		authnetCredit.setMerchant(createMerchant(conference));
		authnetCredit.setMethod(new CreditCardMethod());
		authnetCredit.setTransactionResult(createTransactionResult(payment));
		authnetCredit.setUrl(crsProperties.getProperty("authnetUrl"));
		
		try
		{
			authnetCredit.execute();
		}
		catch(AuthnetTransactionException e)
		{
			/* if the credit failed because the transaction is not yet settled, the transaction can be voided, accomplishing
			 * the same end result.  54 is the reason code that among other things indicates that a transaction cannot be
			 * refunded b/c it has not yet been settled.
			 */
			if(new Integer(54).equals(e.getReasonCode()))
			{
				return voidCreditCardTransaction(conference,payment);
			}
			else throw e;
		}
		
		return authnetCredit.getTransactionResult().getTransactionID();
	}
	
	private String voidCreditCardTransaction(Conference conference, Payment payment) throws IOException
	{
		Void authnetVoid = new Void(new CreditCardMethod());
		
		authnetVoid.setAmount(payment.getAmount());
		authnetVoid.setCreditCard(createCreditCardLastFourDigitsOnly(payment));
		authnetVoid.setGatewayConfiguration(createGatewayConfiguration());
		authnetVoid.setHttpProvider(httpProvider);
		authnetVoid.setLog(Logger.getLogger(this.getClass()));
		authnetVoid.setMerchant(createMerchant(conference));
		authnetVoid.setMethod(new CreditCardMethod());
		authnetVoid.setTransactionResult(createTransactionResult(payment));
		authnetVoid.setUrl(crsProperties.getProperty("authnetUrl"));
		
		authnetVoid.execute();
		
		return authnetVoid.getTransactionResult().getTransactionID();
	}
	
	TransactionResult createTransactionResult(Payment payment)
	{
		TransactionResult transactionResult = new TransactionResult();
		transactionResult.setTransactionID(payment.getCreditCard().getAuthnetTransactionId());
		
		return transactionResult;
	}

	CreditCard createCreditCard(Payment payment)
	{
		CreditCard creditCard = new CreditCard();
		
		creditCard.setCardNumber(payment.getCreditCard().getNumber());
		creditCard.setExpirationDate(new DateTime(DateTimeZone.UTC).withYear(Integer.parseInt(payment.getCreditCard().getExpirationYear()))
												   .withMonthOfYear(Integer.parseInt(payment.getCreditCard().getExpirationMonth()))
												   .dayOfMonth().withMaximumValue()
												   .secondOfDay().withMaximumValue()
												   .toDate());
		
		creditCard.setCardCode(payment.getCreditCard().getCvvNumber());
		
		
		return creditCard;
	}
	
	CreditCard createCreditCardLastFourDigitsOnly(Payment payment)
	{
		CreditCard creditCard = new CreditCard();
		
		creditCard.setCardNumber(payment.getCreditCard().getLastFourDigits());
		
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
		ConferenceCostsEntity conferenceCostsEntity = conferenceCostsService.fetchBy(conference.getId());

		Preconditions.checkNotNull(conferenceCostsEntity.getAuthnetId());
		Preconditions.checkNotNull(conferenceCostsEntity.getAuthnetToken());
		
		Merchant merchant = new Merchant();
		
		merchant.setEmail(conference.getContactPersonEmail());
		merchant.setLogin(conference.getAuthnetId());
		merchant.setTranKey(conference.getAuthnetToken());
		return merchant;
	}
}
