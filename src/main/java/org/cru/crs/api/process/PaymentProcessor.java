package org.cru.crs.api.process;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.errors.BadRequest;
import org.cru.crs.auth.UnauthorizedException;
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

	public org.cru.crs.api.model.Error process(Payment payment, CrsApplicationUser loggedInUser) throws IOException, UnauthorizedException
    {
    	/*make sure the payment is not processed twice in case the client didn't record the fact it was processed*/
    	PaymentEntity copyOfPaymentFromDatabase = paymentService.fetchPaymentBy(payment.getId());
    	if(copyOfPaymentFromDatabase.getTransactionTimestamp() == null && copyOfPaymentFromDatabase.getAuthnetTransactionId() == null)
    	{
    		org.cru.crs.api.model.Error errorMessage = validatePaymentReadiness(payment);
    		if(errorMessage != null) return errorMessage;
    		
    		ConferenceEntity dbConference = conferenceService.fetchConferenceBy(registrationService.getRegistrationBy(payment.getRegistrationId()).getConferenceId());
    		ConferenceCostsEntity dbConferenceCosts = conferenceCostsService.fetchBy(dbConference.getConferenceCostsId());
    		
    		Long transactionId = paymentProcess.processCreditCardTransaction(Conference.fromDb(dbConference, dbConferenceCosts), payment);

    		payment.setAuthnetTransactionId(transactionId);
    		payment.setTransactionDatetime(clock.currentDateTime());

    		paymentService.updatePayment(payment.toJpaPaymentEntity());
    		
    		if(!conferenceWasPaidInFull(payment.getRegistrationId(), dbConferenceCosts))
    		{
    			/*if there is still a balance to be paid, the create a new payment record to capture it.*/
    			paymentService.createPaymentRecord(new PaymentEntity().setId(UUID.randomUUID()).setRegistrationId(payment.getRegistrationId()));
    		}
    	}
    	
    	return null;
    }


	private boolean conferenceWasPaidInFull(UUID registrationId, ConferenceCostsEntity dbConferenceCosts)
	{
		List<PaymentEntity> paymentsForCurrentRegistration = paymentService.fetchPaymentsForRegistration(registrationId);
		
		BigDecimal totalPaid = new BigDecimal(0);
		
		for(PaymentEntity payment : paymentsForCurrentRegistration)
		{
			if(payment.getAuthnetTransactionId() != null)
			{
				totalPaid = totalPaid.add(payment.getAmount());
			}
		}
		
		return totalPaid.compareTo(dbConferenceCosts.getBaseCost()) >= 0;
	}

	private org.cru.crs.api.model.Error validatePaymentReadiness(Payment payment)
	{
		if(payment.getCreditCardCVVNumber() == null)
		{
			return new BadRequest().setCustomErrorMessage("CVV is missing.  Please enter your card security code.");
		}
		
		if(payment.getCreditCardNumber() == null)
		{
			return new BadRequest().setCustomErrorMessage("Credit card number is missing.  Please enter your credit card number.");
		}
		
		if(payment.getCreditCardExpirationMonth() == null)
		{
			return new BadRequest().setCustomErrorMessage("Credit card expiration month is missing.  Please enter your card's expiration month.");
		}
		
		if(payment.getCreditCardExpirationMonth() == null)
		{
			return new BadRequest().setCustomErrorMessage("Credit card expiration year is missing.  Please enter your card's expiration year.");
		}
		
		return null;
	}
}
