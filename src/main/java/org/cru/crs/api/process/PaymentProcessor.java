package org.cru.crs.api.process;

import java.math.BigDecimal;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Payment;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.payment.authnet.Authnet;
import org.cru.crs.payment.authnet.AuthnetPaymentProcess;
import org.cru.crs.payment.authnet.AuthnetTransactionException;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;

import com.google.common.base.Preconditions;

public class PaymentProcessor
{
	
	RegistrationService registrationService;
	PaymentService paymentService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	Clock clock;
	
	AuthorizationService authorizationService;
	
	AuthnetPaymentProcess paymentProcess;
	
	@Inject
	public PaymentProcessor(RegistrationService registrationService,
			PaymentService paymentService, ConferenceService conferenceService,
			ConferenceCostsService conferenceCostsService, Clock clock,
			AuthorizationService authorizationService,
			@Authnet AuthnetPaymentProcess paymentProcess) {
		this.registrationService = registrationService;
		this.paymentService = paymentService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.clock = clock;
		this.authorizationService = authorizationService;
		this.paymentProcess = paymentProcess;
	}

	public void process(Payment payment, CrsApplicationUser loggedInUser)
    {
		Preconditions.checkState(payment.getPaymentType() == PaymentType.CREDIT_CARD);
		
		/*make sure the payment is not processed twice in case the client didn't record the fact it was processed*/
		PaymentEntity copyOfPaymentFromDatabase = paymentService.getPaymentById(payment.getId());
		if(copyOfPaymentFromDatabase.getTransactionTimestamp() == null && copyOfPaymentFromDatabase.getAuthnetTransactionId() == null)
		{
			validatePaymentReadiness(payment);

			ConferenceEntity dbConference = getConferenceForThisPayment(payment);
			ConferenceCostsEntity dbConferenceCosts = getConferenceCostsForThisPayment(dbConference);

			try
			{
				String transactionId = paymentProcess.processCreditCardTransaction(Conference.fromDb(dbConference, dbConferenceCosts), payment);

				payment.getCreditCard().setAuthnetTransactionId(transactionId);
				payment.setTransactionDatetime(clock.currentDateTime());
			}
			catch(AuthnetTransactionException e)
			{
				throw new WebApplicationException(e.getMessage(), e, 502);
			}
			catch(Exception e)
			{
				throw new WebApplicationException(e, 502);
			}

			paymentService.updatePayment(payment.toDbPaymentEntity(), loggedInUser);
		}
    }

	/**
	 * Process the refund.  All refunds are done against an already processed payment.
	 * 
	 * @param refund
	 * @param loggedInAdmin
	 */
	public void processRefund(Payment refund, CrsApplicationUser loggedInAdmin)
	{
		Preconditions.checkState(refund.getPaymentType() == PaymentType.CREDIT_CARD_REFUND);
		
		if(refund.getRefundedPaymentId() == null) throw new BadRequestException("the ID of payment to refund is required");
		
		//load out the original payment, the client should specify this payment by setting its ID in the field refundedPaymentId.
		PaymentEntity paymentToRefund = paymentService.getPaymentById(refund.getRefundedPaymentId());
		
		if(paymentToRefund == null) throw new BadRequestException("the payment being refunded does not exist");
		
		BigDecimal amountAlreadyRefunded = addPreviouslyRefundedAmount(paymentToRefund);
		
		/* make sure the amount being refunded now, plus any amounts already refunded don't exceed
		 * the existing payment amount.  Authnet will check for us, but better to check ourselves */
		if(refund.getAmount().add(amountAlreadyRefunded).compareTo(paymentToRefund.getAmount()) > 0)
		{
			throw new BadRequestException("cannot refund more than the original payment amount");
		}
		
		ConferenceEntity dbConference = getConferenceForThisPayment(refund);
		ConferenceCostsEntity dbConferenceCosts = getConferenceCostsForThisPayment(dbConference);

		refund.getCreditCard().setAuthnetTransactionId(paymentToRefund.getAuthnetTransactionId());
		
		try
		{
			String refundTransactionId = paymentProcess.processCreditCardRefund(Conference.fromDb(dbConference, dbConferenceCosts), refund);

			refund.getCreditCard().setAuthnetTransactionId(refundTransactionId);
			refund.setTransactionDatetime(clock.currentDateTime());
		}
		catch(AuthnetTransactionException e)
		{
			throw new WebApplicationException(e.getMessage(), e, 502);
		}
		catch(Exception e)
		{
			throw new WebApplicationException(e, 502);
		}
		
		paymentService.updatePayment(refund.toDbPaymentEntity(), loggedInAdmin);
	}

	public void saveNewPayment(Payment payment, CrsApplicationUser loggedInUser)
	{
		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(payment.getRegistrationId());

		authorizationService.authorizeRegistration(registrationEntity, 
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()),
				OperationType.UPDATE,
				loggedInUser);

		if(paymentService.getPaymentById(payment.getId()) == null)
		{
			if(payment.getId() == null) payment.setId(UUID.randomUUID());
			paymentService.createPaymentRecord(payment.toDbPaymentEntity(), loggedInUser);
		}
		else
		{
			throw new BadRequestException("Payment with id: " + payment.getId() + " already exists.");
		}
	}
	
	public void processPayment(Payment payment, CrsApplicationUser loggedInUser)
	{
		if(payment.isReadyToProcess())
		{
			if(payment.getPaymentType() == PaymentType.CREDIT_CARD)
			{
				process(payment, loggedInUser);
			}
			else if(payment.getPaymentType() == PaymentType.CREDIT_CARD_REFUND)
			{
				processRefund(payment, loggedInUser);
			}
			else
            {
                PaymentEntity databasePayment = payment.toDbPaymentEntity();
                databasePayment.setTransactionTimestamp(clock.currentDateTime());
                paymentService.updatePayment(databasePayment, loggedInUser);
            }
		}
	}
	
	private BigDecimal addPreviouslyRefundedAmount(PaymentEntity paymentToRefund)
	{
		BigDecimal amountAlreadyRefunded = new BigDecimal("0");
		
		for(PaymentEntity previousRefund : paymentService.getPaymentsByRefundedPaymentId(paymentToRefund.getId()))
		{
			if(previousRefund.getAuthnetTransactionId() != null && previousRefund.getTransactionTimestamp() != null)
			{
				amountAlreadyRefunded = amountAlreadyRefunded.add(previousRefund.getAmount());
			}
		}
		return amountAlreadyRefunded;
	}

	private void validatePaymentReadiness(Payment payment)
	{
		if(payment.getCreditCard().getCvvNumber() == null)
		{
			throw new BadRequestException("CVV is missing.  Please enter your card security code.");
		}
		
		if(payment.getCreditCard().getNumber() == null)
		{
			throw new BadRequestException("Credit card number is missing.  Please enter your credit card number.");
		}
		
		if(payment.getCreditCard().getExpirationMonth() == null)
		{
			throw new BadRequestException("Credit card expiration month is missing.  Please enter your card's expiration month.");
		}
		
		if(payment.getCreditCard().getExpirationMonth() == null)
		{
			throw new BadRequestException("Credit card expiration year is missing.  Please enter your card's expiration year.");
		}
	}
	
	private ConferenceCostsEntity getConferenceCostsForThisPayment(ConferenceEntity dbConference)
	{
		return conferenceCostsService.fetchBy(dbConference.getConferenceCostsId());
	}

	private ConferenceEntity getConferenceForThisPayment(Payment payment)
	{
		return conferenceService.fetchConferenceBy(registrationService.getRegistrationBy(payment.getRegistrationId()).getConferenceId());
	}

}
