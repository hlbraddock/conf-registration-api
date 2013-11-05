package org.cru.crs.api.process;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.CollectionUtils;
import org.testng.collections.Lists;
import org.testng.internal.annotations.Sets;

public class RegistrationUpdateProcess
{
	RegistrationService registrationService;
	AnswerService answerService;
	PaymentService paymentService;
	ConferenceService conferenceService;
	
	RegistrationEntity originalRegistrationEntity;
	Set<AnswerEntity> originalAnswerEntitySet;
	
	public RegistrationUpdateProcess(RegistrationService registrationService,AnswerService answerService, PaymentService paymentService,ConferenceService conferenceService)
	{
		this.registrationService = registrationService;
		this.answerService = answerService;
		this.paymentService = paymentService;
		this.conferenceService = conferenceService;
	}
	
	public void performDeepUpdate(Registration registration)
	{
		originalRegistrationEntity = registrationService.getRegistrationBy(registration.getId());
		originalAnswerEntitySet = getAnswerEntitySetFromDb(registration);
		
		if(originalRegistrationEntity == null)
		{
			registrationService.createNewRegistration(originalRegistrationEntity);
		}
		
		handleMissingAnswers(registration);
		
		for(Answer updatedOrNewAnswer : registration.getAnswers())
		{
			if(originalAnswerEntitySet.contains(updatedOrNewAnswer.toDbAnswerEntity()))
			{
				answerService.updateAnswer(updatedOrNewAnswer.toDbAnswerEntity());
			}
			else
			{
				answerService.insertAnswer(updatedOrNewAnswer.toDbAnswerEntity());
			}
		}
		
		registrationService.updateRegistration(registration.toDbRegistrationEntity());
	}

	private void handleMissingAnswers(Registration registration)
	{
		Collection<AnswerEntity> deletedAnswerEntites = CollectionUtils.firstNotFoundInSecond(Lists.newArrayList(originalAnswerEntitySet), 
																								Lists.newArrayList(convertWebAnswersToAnswerEntities(registration.getAnswers())));
		
		for(AnswerEntity answerToDelete : deletedAnswerEntites)
		{
			answerService.deleteAnswer(answerToDelete);
		}
	}

	private  List<AnswerEntity> convertWebAnswersToAnswerEntities(Set<Answer> answers)
	{
		List<AnswerEntity> answerEntities = Lists.newArrayList();
	
		if(answers == null) return answerEntities;
		
		for(Answer webAnswer : answers)
		{
			answerEntities.add(webAnswer.toDbAnswerEntity());
		}
		return answerEntities;
	}

	private Set<AnswerEntity> getAnswerEntitySetFromDb(Registration registration)
	{
		Set<AnswerEntity> answers = Sets.newHashSet();
		answers.addAll(answerService.getAllAnswersForRegistration(registration.getId()));
		return answers;
	}
}
