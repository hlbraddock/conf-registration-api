package org.cru.crs.api.model.utils;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;

public class RegistrationAssembler
{

	public static Registration buildRegistration(UUID registrationId, RegistrationService registrationService, PaymentService paymentService, AnswerService answerService)
	{
		RegistrationEntity databaseRegistration = registrationService.getRegistrationBy(registrationId);
		List<PaymentEntity> databasePayments = paymentService.fetchPaymentsForRegistration(registrationId);
		List<AnswerEntity> databaseAnswers = answerService.getAllAnswersForRegistration(registrationId);
		PaymentEntity currentPayment = null;
		
		if(databasePayments != null)
		{
			Iterator<PaymentEntity> i = databasePayments.iterator();
			for(; i.hasNext();)
			{
				PaymentEntity payment = i.next();
				if(payment.getAuthnetTransactionId() == null && payment.getTransactionTimestamp() == null)
				{
					currentPayment = payment;
					i.remove();
					break;
				}
			}
		}
		return Registration.fromDb(databaseRegistration, databaseAnswers, databasePayments, currentPayment);
	}
}
