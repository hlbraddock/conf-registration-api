package org.cru.crs.api.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.domain.ProfileAttribute;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.CollectionUtils;
import org.jboss.logging.Logger;
import org.testng.collections.Lists;
import org.testng.internal.annotations.Sets;

public class RegistrationUpdateProcess
{
	RegistrationService registrationService;
	AnswerService answerService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	BlockService blockService;
	UserService userService;
	Clock clock;
	
	RegistrationEntity originalRegistrationEntity;
	Set<AnswerEntity> originalAnswerEntitySet;

	Logger logger = Logger.getLogger(RegistrationUpdateProcess.class);

	@Inject
	public RegistrationUpdateProcess(RegistrationService registrationService, AnswerService answerService, ConferenceService conferenceService, ConferenceCostsService conferenceCostsService, Clock clock, BlockService blockService, UserService userService)
	{
		this.registrationService = registrationService;
		this.answerService = answerService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
		this.clock = clock;
		this.blockService = blockService;
		this.userService = userService;
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

		captureUserProfile(registration);

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
	
	private void recordCompletedTimestampIfThisUpdateCompletesRegistration(RegistrationEntity registration)
	{
		/*this should only be done once, that's when the updated registration is completed by the version
		stored in the database is not*/
		if(registration.nullSafeIsCompleted() && !originalRegistrationEntity.nullSafeIsCompleted())
		{
			registration.setCompletedTimestamp(clock.currentDateTime());
		}
	}
	
	/**
	 * If the registration is completed, let's check 
	 */
	private void setTotalDueBasedOnCompletedTimeAndEarlyRegistrationFactors(RegistrationEntity updatedRegistration)
	{
		/*this should only be done once, that's when the updated registration is completed by the version
		stored in the database is not*/
		if(updatedRegistration.nullSafeIsCompleted() && !originalRegistrationEntity.nullSafeIsCompleted())
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

	private void captureUserProfile(Registration registration)
	{
		UserEntity userEntity = userService.fetchUserBy(registration.getUserId());

		if(userEntity == null)
		{
			logger.error("Could not capture user profile information, user entity is null for id " + registration.getUserId());

			return;
		}

		List<ProfileAttribute> profileAttributes = profileAttributesFromAnswers(registration.getAnswers());

		userEntity.setProfileAttributes(profileAttributes);

		userService.updateUser(userEntity);
	}

	private List<ProfileAttribute> profileAttributesFromAnswers(Set<Answer> answers)
	{
		List<ProfileAttribute> profileAttributes = new ArrayList<ProfileAttribute>();

		for (Answer answer : answers)
		{
			BlockEntity blockEntity = blockService.fetchBlockBy(answer.getBlockId());

			if (blockEntity.getProfileType() != null &&
					!blockEntity.getProfileType().equals(ProfileAttribute.Type.none))
			{
				profileAttributes.add(new ProfileAttribute(blockEntity.getProfileType(), answer.getValue().getTextValue()));
			}
		}

		return profileAttributes;
	}
}
