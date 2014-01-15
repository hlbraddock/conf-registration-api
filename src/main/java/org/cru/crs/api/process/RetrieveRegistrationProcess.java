package org.cru.crs.api.process;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;

public class RetrieveRegistrationProcess
{
	RegistrationService registrationService;
	PaymentService paymentService;
	AnswerService answerService;
	
	@Inject
	public RetrieveRegistrationProcess(RegistrationService registrationService, PaymentService paymentService, AnswerService answerService)
	{
		this.registrationService = registrationService;
		this.paymentService = paymentService;
		this.answerService = answerService;
	}

	public Registration get(UUID registrationId)
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
