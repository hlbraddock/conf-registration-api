package org.cru.crs.api.process;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.jaxrs.UnauthorizedException;
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

public class UpdateRegistrationProcess
{
	RegistrationService registrationService;
	AnswerService answerService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	ProfileProcess profileProcess;
	Clock clock;
	
	AuthorizationService authorizationService;

	@Inject
	public UpdateRegistrationProcess(RegistrationService registrationService, AnswerService answerService, ConferenceService conferenceService, ConferenceCostsService conferenceCostsService, Clock clock, AuthorizationService authorizationService, ProfileProcess profileProcess)
	{
		this.registrationService = registrationService;
		this.answerService = answerService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.clock = clock;
		this.authorizationService = authorizationService;
		this.profileProcess = profileProcess;
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

		/*switch to a RegistrationEntity now b/c we're going to do some calculations the client
		 * cannot influence to determine total cost and completed timestamp*/
		RegistrationEntity registrationEntity = registration.toDbRegistrationEntity();

		recordCompletedTimestampIfThisUpdateCompletesRegistration(registrationEntity, originalRegistrationEntity);
		
		calculateTotalDueBasedOnCompletedTimeAndEarlyRegistrationFactors(registrationEntity, originalRegistrationEntity);
		
		/* administrators can override the total due if they need to for some reason. the proper auth
		 * checks are checked in this method*/
		administratorOverrideOfTotalDue(registration, registrationEntity, loggedInAdmin);
		
		ensureStoredValuesDontGetErased(registrationEntity, originalRegistrationEntity);
		
		registrationService.updateRegistration(registrationEntity);
	}

	private void ensureStoredValuesDontGetErased(RegistrationEntity registrationEntity, RegistrationEntity originalRegistrationEntity)
	{
		if(registrationEntity.getCompletedTimestamp() == null && originalRegistrationEntity.getCompletedTimestamp() != null)
		{
			registrationEntity.setCompletedTimestamp(originalRegistrationEntity.getCompletedTimestamp());
		}
		
		if(registrationEntity.getTotalDue() == null && originalRegistrationEntity.getTotalDue() != null) 
		{
			registrationEntity.setTotalDue(originalRegistrationEntity.getTotalDue());
		}
	}

	/**
	 * If the person who is logged in is has update rights on the conference, he/she has the ability to update the total amount due.
	 * 
	 * @param registration
	 * @param registrationEntity
	 * @param loggedInAdmin
	 * @return
	 */
	private void administratorOverrideOfTotalDue(Registration registration, RegistrationEntity registrationEntity, CrsApplicationUser loggedInAdmin)
	{
		try 
		{
			authorizationService.authorizeConference(
										conferenceService.fetchConferenceBy(registration.getConferenceId()), 
										OperationType.UPDATE,
										loggedInAdmin);
			
			/*I don't think an admin should be able to update their own registration's totalDue.  Someone else should have to do that
			 * for them, for accountability. */
			if(loggedInAdmin.getId().equals(registration.getUserId())) throw new UnauthorizedException();
			
			//make sure that the amount coming over is non-null, and greater than 'zero'.  either of these values
			//suggest the client didn't send an amount over and we don't want to accidentally make the conference free
			if(registration.getTotalDue() != null && registration.getTotalDue().compareTo(new BigDecimal("0")) > 0)
			{
				registrationEntity.setTotalDue(registration.getTotalDue());
			}
		}
		catch(UnauthorizedException e){ /*do nothing*/ }
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
	
	/**
	 * If the registration is completed, let's check 
	 */
	private void calculateTotalDueBasedOnCompletedTimeAndEarlyRegistrationFactors(RegistrationEntity updatedRegistration, RegistrationEntity originalRegistrationEntity)
	{
		if(updatedRegistration.getCompleted()  && !originalRegistrationEntity.getCompleted())
		{
			ConferenceCostsEntity conferenceCostsEntity = conferenceCostsService.fetchBy(updatedRegistration.getConferenceId());

			if(conferenceCostsEntity.isEarlyRegistrationDiscount() && updatedRegistration.getCompletedTimestamp().isBefore(conferenceCostsEntity.getEarlyRegistrationCutoff()))
			{
				updatedRegistration.setTotalDue(conferenceCostsEntity.getBaseCost().subtract(conferenceCostsEntity.getEarlyRegistrationAmount()));
			}
			else
			{
				updatedRegistration.setTotalDue(conferenceCostsEntity.getBaseCost());
			}
		}
	}
}
