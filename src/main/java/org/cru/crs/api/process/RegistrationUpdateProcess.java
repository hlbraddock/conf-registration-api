package org.cru.crs.api.process;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.CollectionUtils;
import org.testng.collections.Lists;
import org.testng.internal.annotations.Sets;

public class RegistrationUpdateProcess
{
	RegistrationService registrationService;
	AnswerService answerService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	Clock clock;
	
	RegistrationEntity originalRegistrationEntity;
	Set<AnswerEntity> originalAnswerEntitySet;
	
	@Inject
	public RegistrationUpdateProcess(RegistrationService registrationService, AnswerService answerService, ConferenceService conferenceService, ConferenceCostsService conferenceCostsService, Clock clock)
	{
		this.registrationService = registrationService;
		this.answerService = answerService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.clock = clock;
	}
	
	public void performDeepUpdate(Registration registration)
	{
		originalRegistrationEntity = registrationService.getRegistrationBy(registration.getId());
		originalAnswerEntitySet = getAnswerEntitySetFromDb(registration);
		
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

		/*switch to a RegistrationEntity now b/c we're going to do some calculations the client
		 * cannot influence to determine total cost and completed timestamp*/
		RegistrationEntity registrationEntity = registration.toDbRegistrationEntity();

		recordCompletedTimestampIfThisUpdateCompletesRegistration(registrationEntity);
		setTotalDueBasedOnCompletedTimeAndEarlyRegistrationFactors(registrationEntity);
		
		registrationService.updateRegistration(registrationEntity);
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
	
	private void recordCompletedTimestampIfThisUpdateCompletesRegistration(RegistrationEntity registration)
	{
		if(registration.getCompleted() == null) return;
		if(originalRegistrationEntity == null) return;
		if(originalRegistrationEntity.getCompleted() == null) return;
		
		if(registration.getCompleted() && !originalRegistrationEntity.getCompleted())
		{
			registration.setCompletedTimestamp(clock.currentDateTime());
		}
	}
	
	/**
	 * If the conference is completed, let's check 
	 * @param registration
	 */
	private void setTotalDueBasedOnCompletedTimeAndEarlyRegistrationFactors(RegistrationEntity registration)
	{
		// if the registration is not completed yet, no reason to proceed
		if(registration.getCompleted() == null) return;
		if(originalRegistrationEntity == null) return;
		if(originalRegistrationEntity.getCompleted() == null) return;
		
		ConferenceCostsEntity conferenceCostsEntity = conferenceCostsService.fetchBy(registration.getConferenceId());
		
		if(conferenceCostsEntity.isAcceptCreditCards())
		{
			if(conferenceCostsEntity.isEarlyRegistrationDiscount() && registration.getCompletedTimestamp().isBefore(conferenceCostsEntity.getEarlyRegistrationCutoff()))
			{
				registration.setTotalDue(conferenceCostsEntity.getBaseCost().subtract(conferenceCostsEntity.getEarlyRegistrationAmount()));
			}
			else
			{
				registration.setTotalDue(conferenceCostsEntity.getBaseCost());
			}
		}
	}
}
