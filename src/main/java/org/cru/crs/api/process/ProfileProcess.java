package org.cru.crs.api.process;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.ccci.util.strings.Strings;

import org.cru.crs.api.model.answer.AddressQuestion;
import org.cru.crs.api.model.answer.BlockType;
import org.cru.crs.api.model.answer.DateQuestion;
import org.cru.crs.api.model.answer.NameQuestion;
import org.cru.crs.api.model.answer.TextQuestion;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.JsonNodeHelper;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @ lee.braddock
 */
public class ProfileProcess
{
	private BlockService blockService;
	private ProfileService profileService;
	private PageService pageService;
	private UserService userService;

	Logger logger = Logger.getLogger(ProfileProcess.class);

	@Inject
	public ProfileProcess(BlockService blockService, ProfileService profileService, PageService pageService, UserService userService)
	{
		this.blockService = blockService;
		this.profileService = profileService;
		this.pageService = pageService;
		this.userService = userService;
	}

	public void capture(Registration registration, AuthenticationProviderType authenticationProviderType)
	{
		ProfileEntity profileEntity = getUserProfile(registration.getUserId());

		profileEntity.set(getAnswerBlockMap(registration.getAnswers()));

		Simply.logObject(profileEntity, this.getClass());

		profileService.updateProfile(profileEntity);

		// capture the anonymous user's email address while you're at it
		if(authenticationProviderType.equals(AuthenticationProviderType.NONE))
		{
			UserEntity userEntity = userService.getUserById(registration.getUserId());

			userEntity.setEmailAddress(profileEntity.getEmail());

			userService.updateUser(userEntity);
		}
	}

	private ProfileEntity getUserProfile(UUID userId)
	{
		ProfileEntity profileEntity = profileService.getProfileByUser(userId);

		if(profileEntity == null)
		{
			profileEntity = new ProfileEntity(UUID.randomUUID(), userId);

			profileService.createProfile(profileEntity);
		}

		return profileEntity;
	}

	public void populateRegistrationAnswers(Registration registration)
	{
		ProfileEntity profileEntity = profileService.getProfileByUser(registration.getUserId());

		if(profileEntity == null)
			return;

		Simply.logObject(profileEntity, ProfileProcess.class);

		setAnswersFromProfileEntity(registration, profileEntity);
	}

	private Map<Answer, BlockEntity> getAnswerBlockMap(Set<Answer> answers)
	{
		Map<Answer,BlockEntity> answerBlockEntityHashMap = new HashMap<Answer, BlockEntity>();

		for (Answer answer : answers)
		{
			answerBlockEntityHashMap.put(answer, blockService.getBlockById(answer.getBlockId()));
		}

		return answerBlockEntityHashMap;
	}

	public Set<BlockEntity> fetchBlocksForConference(UUID conferenceId)
	{
		Set<BlockEntity> blockEntities = new HashSet<BlockEntity>();

		List<PageEntity> pageEntities = pageService.fetchPagesForConference(conferenceId);
		for(PageEntity pageEntity : pageEntities)
			blockEntities.addAll(blockService.fetchBlocksForPage(pageEntity.getId()));

		return blockEntities;
	}

	private void setAnswersFromProfileEntity(Registration registration, ProfileEntity profileEntity)
	{
		Set<UUID> blocksWithAnswers = getBlocksWithAnswers(registration.getAnswers());

		Set<BlockEntity> blockEntities = fetchBlocksForConference(registration.getConferenceId());

		for(BlockEntity	blockEntity : blockEntities)
		{
			// don't overwrite existing answers
			if(blocksWithAnswers.contains(blockEntity.getId()))
				continue;

			// if the block has a profile type, add an answer to the list with the appropriate profile value
			if (hasProfileType(blockEntity))
			{
				try
				{
					JsonNode jsonNode = null;

					BlockType blockType = BlockType.fromString(blockEntity.getBlockType());

					// serialize the appropriate block type java object into a json formatted string
					if(blockType.isTextQuestion())
					{
						TextQuestion textQuestion = profileEntity.getTextQuestion(blockEntity.getProfileType());
						if(!textQuestion.isEmpty())
							jsonNode = JsonNodeHelper.serialize(textQuestion);
					}

					else if(blockType.isDateQuestion())
					{
						DateQuestion dateQuestion = profileEntity.getDateQuestion(blockEntity.getProfileType());
						if(!dateQuestion.isEmpty())
							jsonNode = JsonNodeHelper.serialize(dateQuestion);
					}

					else if(blockType.isNameQuestion())
					{
						NameQuestion nameQuestion = profileEntity.getNameQuestion();
						if(!nameQuestion.isEmpty())
							jsonNode = JsonNodeHelper.serialize(nameQuestion);
					}

					else if(blockType.isAddressQuestion())
					{
						AddressQuestion addressQuestion = profileEntity.getAddressQuestion();
						if(addressQuestion.isEmpty())
							jsonNode = JsonNodeHelper.serialize(addressQuestion);
					}

					// if unrecognizable block type
					else return;

					// construct the answer
					Answer answer = new Answer(UUID.randomUUID(), registration.getId(), blockEntity.getId(), jsonNode);

					// add answer to the registration
					registration.getAnswers().add(answer);
				}
				catch(Exception e)
				{
					logger.error("Could not pre-populate registration for block type " + blockEntity.getBlockType() +
									" and profile type " + blockEntity.getProfileType(), e);
				}
			}
		}
	}

	private Set<UUID> getBlocksWithAnswers(Set<Answer> answers)
	{
		Set<UUID> blocksWithAnswers = new HashSet<UUID>();

		for (Answer answer : answers)
		{
			BlockEntity blockEntity = blockService.getBlockById(answer.getBlockId());

			if (hasProfileType(blockEntity))
			{
				blocksWithAnswers.add(blockEntity.getId());
			}
		}

		return blocksWithAnswers;
	}

	public boolean hasProfileType(BlockEntity blockEntity)
	{
		return blockEntity.getProfileType() != null && !Strings.isEmpty(blockEntity.getProfileType().toString());
	}
}
