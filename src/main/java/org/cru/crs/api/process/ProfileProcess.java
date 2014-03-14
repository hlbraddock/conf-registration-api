package org.cru.crs.api.process;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.ccci.util.strings.Strings;

import org.cru.crs.api.model.answer.AddressQuestion;
import org.cru.crs.api.model.answer.BlockType;
import org.cru.crs.api.model.answer.DateQuestion;
import org.cru.crs.api.model.answer.NameQuestion;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.model.ProfileType;
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

	public void capture(Registration registration)
	{
		ProfileEntity profileEntity = getUserProfile(registration.getUserId());

		Simply.logObject(profileEntity, getClass());

		profileEntity.set(getAnswerBlockEntityMap(registration.getAnswers()));

		Simply.logObject(profileEntity, this.getClass());

		profileService.updateProfile(profileEntity);

		// populate user entity from profile
		userService.updateUser(userService.getUserById(registration.getUserId()).set(profileEntity, true /* if not already set */));
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

	public Map<Answer, BlockEntity> getAnswerBlockEntityMap(Set<Answer> answers)
	{
		Map<Answer,BlockEntity> answerBlockEntityMap = new HashMap<Answer, BlockEntity>();

		for (Answer answer : answers)
		{
			answerBlockEntityMap.put(answer, blockService.getBlockById(answer.getBlockId()));
		}

		return answerBlockEntityMap;
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

					if (blockType.isJsonFormat())
					{
						if (blockType.equals(BlockType.NAME_QUESTION))
						{
							NameQuestion nameQuestion = new NameQuestion();

							nameQuestion.setFirstName(profileEntity.getFirstName());
							nameQuestion.setLastName(profileEntity.getLastName());

							if (!nameQuestion.isEmpty())
								jsonNode = JsonNodeHelper.serialize(nameQuestion);
						}
						else if (blockType.equals(BlockType.ADDRESS_QUESTION))
						{
							AddressQuestion addressQuestion = new AddressQuestion();

							addressQuestion.setAddress1(profileEntity.getAddress1());
							addressQuestion.setAddress2(profileEntity.getAddress2());
							addressQuestion.setCity(profileEntity.getCity());
							addressQuestion.setState(profileEntity.getState());
							addressQuestion.setZip(profileEntity.getZip());

							if (!addressQuestion.isEmpty())
								jsonNode = JsonNodeHelper.serialize(addressQuestion);
						}
					}
					else if (blockType.isTextFormat())
					{
						if (blockType.equals(BlockType.DATE_QUESTION))
						{
							DateQuestion dateQuestion = new DateQuestion();

							if (blockEntity.getProfileType().equals(ProfileType.BIRTH_DATE))
								dateQuestion.setText(profileEntity.getBirthDate());
							else if (blockEntity.getProfileType().equals(ProfileType.GRADUATION))
								dateQuestion.setText(profileEntity.getGraduation());

							if(dateQuestion.getText() != null)
								jsonNode = JsonNodeHelper.toJsonNode(JsonNodeHelper.serialize(dateQuestion).get("text").toString());
						}
						else
						{
							String value = null;
							switch (blockEntity.getProfileType())
							{
								case CAMPUS:
									value = profileEntity.getCampus();
									break;
								case DORMITORY:
									value = profileEntity.getDormitory();
									break;
								case EMAIL:
									value = profileEntity.getEmail();
									break;
								case GENDER:
									value = profileEntity.getGender();
									break;
								case PHONE:
									value = profileEntity.getPhone();
									break;
							}

							if(!Strings.isEmpty(value))
								jsonNode = JsonNodeHelper.toJsonNode(JsonNodeHelper.toJsonString(value));
						}
					}

					// if unrecognizable block type
					else return;

					// add answer to the registration
					if (jsonNode != null)
					{
						Answer answer = new Answer(UUID.randomUUID(), registration.getId(), blockEntity.getId(), jsonNode);
						registration.getAnswers().add(answer);
					}
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
