package org.cru.crs.api.process;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.payment.authnet.Authnet;
import org.cru.crs.payment.authnet.AuthnetPaymentProcess;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.jboss.resteasy.spi.BadRequestException;

public class PaymentProcessor
{
	
	RegistrationService registrationService;
	PaymentService paymentService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	Clock clock;
	AuthnetPaymentProcess paymentProcess;
	
	@Inject
	public PaymentProcessor(RegistrationService registrationService,
			PaymentService paymentService, ConferenceService conferenceService,
			ConferenceCostsService conferenceCostsService, Clock clock,
			@Authnet AuthnetPaymentProcess paymentProcess) {
		this.registrationService = registrationService;
		this.paymentService = paymentService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.clock = clock;
		this.paymentProcess = paymentProcess;
	}

	public org.cru.crs.api.model.Error process(Payment payment, CrsApplicationUser loggedInUser) throws IOException
    {
    	/*make sure the payment is not processed twice in case the client didn't record the fact it was processed*/
    	PaymentEntity copyOfPaymentFromDatabase = paymentService.fetchPaymentBy(payment.getId());
    	if(copyOfPaymentFromDatabase.getTransactionTimestamp() == null && copyOfPaymentFromDatabase.getAuthnetTransactionId() == null)
    	{
    		validatePaymentReadiness(payment);
    		
    		ConferenceEntity dbConference = conferenceService.fetchConferenceBy(registrationService.getRegistrationBy(payment.getRegistrationId()).getConferenceId());
    		ConferenceCostsEntity dbConferenceCosts = conferenceCostsService.fetchBy(dbConference.getConferenceCostsId());

    		try
    		{
    			Long transactionId = paymentProcess.processCreditCardTransaction(Conference.fromDb(dbConference, dbConferenceCosts), payment);

    			payment.setAuthnetTransactionId(transactionId);
    			payment.setTransactionDatetime(clock.currentDateTime());
    		}
    		catch(Exception e)
    		{
    			throw new WebApplicationException(e, 502);
    		}
    		
    		paymentService.updatePayment(payment.toJpaPaymentEntity());
    	}
    	
    	return null;
    }

	private void validatePaymentReadiness(Payment payment)
	{
		if(payment.getCreditCardCVVNumber() == null)
		{
			throw new BadRequestException("CVV is missing.  Please enter your card security code.");
		}
		
		if(payment.getCreditCardNumber() == null)
		{
			throw new BadRequestException("Credit card number is missing.  Please enter your credit card number.");
		}
		
		if(payment.getCreditCardExpirationMonth() == null)
		{
			throw new BadRequestException("Credit card expiration month is missing.  Please enter your card's expiration month.");
		}
		
		if(payment.getCreditCardExpirationMonth() == null)
		{
			throw new BadRequestException("Credit card expiration year is missing.  Please enter your card's expiration year.");
		}
	}
}
