package org.cru.crs.api.process;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.utils.TotalDueBusinessLogic;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.CollectionUtils;
import org.testng.collections.Lists;
import org.testng.internal.annotations.Sets;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UpdateRegistrationProcess
{
	RegistrationService registrationService;
	AnswerService answerService;
	ConferenceService conferenceService;
	ProfileProcess profileProcess;
	
	TotalDueBusinessLogic totalDueBusinessLogic;
	
	Clock clock;
	

	@Inject
	public UpdateRegistrationProcess(RegistrationService registrationService, 
										AnswerService answerService, 
										ConferenceService conferenceService, 
										ProfileProcess profileProcess,
										TotalDueBusinessLogic totalDueBusinessLogic,
										Clock clock)
	{
		this.registrationService = registrationService;
		this.answerService = answerService;
		this.conferenceService = conferenceService;
		this.profileProcess = profileProcess;
		this.totalDueBusinessLogic = totalDueBusinessLogic;
		this.clock = clock;
	}
	
	public void performDeepUpdate(Registration registration, CrsApplicationUser loggedInAdmin)
	{
		RegistrationEntity originalRegistrationEntity = registrationService.getRegistrationBy(registration.getId());
		Set<AnswerEntity> originalAnswerEntitySet = getAnswerEntitySetFromDb(registration);
		
		handleMissingAnswers(registration, originalAnswerEntitySet);
		
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

		/* switch to a RegistrationEntity now b/c we're going to do some calculations the client
		 * cannot influence to determine total cost and completed timestamp*/
		RegistrationEntity registrationEntityCopyOfApiRegistration = registration.toDbRegistrationEntity();

		recordCompletedTimestampIfThisUpdateCompletesRegistration(registrationEntityCopyOfApiRegistration, originalRegistrationEntity);
		
		totalDueBusinessLogic.setTotalDue(registration, registrationEntityCopyOfApiRegistration, originalRegistrationEntity, loggedInAdmin);
		
		registrationService.updateRegistration(registrationEntityCopyOfApiRegistration);
	}

	private void handleMissingAnswers(Registration registration, Set<AnswerEntity> originalAnswerEntitySet)
	{
		Collection<AnswerEntity> deletedAnswerEntites = CollectionUtils.firstNotFoundInSecond(Lists.newArrayList(originalAnswerEntitySet), 
																								Lists.newArrayList(convertWebAnswersToAnswerEntities(registration.getAnswers())));
		
		for(AnswerEntity answerToDelete : deletedAnswerEntites)
		{
			answerService.deleteAnswer(answerToDelete.getId());
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
	
	private void recordCompletedTimestampIfThisUpdateCompletesRegistration(RegistrationEntity updatedRegistration, RegistrationEntity originalRegistrationEntity)
	{
		/*this should only be done once, that's when the updated registration is completed by the version
		stored in the database is not*/
		if(updatedRegistration.getCompleted() && !originalRegistrationEntity.getCompleted())
		{
			updatedRegistration.setCompletedTimestamp(clock.currentDateTime());
		}
	}
}
