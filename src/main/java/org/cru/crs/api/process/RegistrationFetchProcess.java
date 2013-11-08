package org.cru.crs.api.process;

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

public class RegistrationFetchProcess
{
	public static Registration buildRegistration(UUID registrationId, RegistrationService registrationService, PaymentService paymentService, AnswerService answerService)
	{
		RegistrationEntity databaseRegistration = registrationService.getRegistrationBy(registrationId);
		List<AnswerEntity> databaseAnswers = answerService.getAllAnswersForRegistration(registrationId);
		List<PaymentEntity> databasePayments = paymentService.fetchPaymentsForRegistration(registrationId);
		
		PaymentEntity currentPayment = null;
		
		Iterator<PaymentEntity> i = databasePayments.iterator();
		
		for(; i.hasNext(); )
		{
			PaymentEntity nextPayment = i.next();
			if(nextPayment.getAuthnetTransactionId() == null)
			{
				i.remove();
				currentPayment = nextPayment;
			}
		}
		
		return Registration.fromDb(databaseRegistration,databaseAnswers,databasePayments,currentPayment);
	}
}
