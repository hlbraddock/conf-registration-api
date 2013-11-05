package org.cru.crs.api.process;

import java.util.List;
import java.util.UUID;

import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.testng.collections.Lists;

public class RegistrationFetchProcess
{
	public static Registration buildRegistration(UUID registrationId, RegistrationService registrationService, PaymentService paymentService, AnswerService answerService)
	{
		RegistrationEntity databaseRegistration = registrationService.getRegistrationBy(registrationId);
		List<AnswerEntity> databaseAnswers = answerService.getAllAnswersForRegistration(registrationId);
		List<PaymentEntity> databasePayments = paymentService.fetchPaymentsForRegistration(registrationId);
		
		List<Payment> pastPayments = Lists.newArrayList();
		PaymentEntity currentPayment = null;
		
		for(PaymentEntity paymentEntity : databasePayments)
		{
			Payment payment = Payment.fromJpa(paymentEntity);
			if(payment.getAuthnetTransactionId() != null) pastPayments.add(payment);
			else currentPayment = paymentEntity;
		}
		
		return Registration.fromDb(databaseRegistration,databaseAnswers,databasePayments,currentPayment);
	}
}
