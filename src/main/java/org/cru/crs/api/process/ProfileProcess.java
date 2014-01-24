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
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.utils.JsonNodeHelper;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
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

	Logger logger = Logger.getLogger(ProfileProcess.class);

	@Inject
	public ProfileProcess(BlockService blockService, ProfileService profileService, PageService pageService)
	{
		this.blockService = blockService;
		this.profileService = profileService;
		this.pageService = pageService;
	}

	public void capture(Registration registration)
	{
		ProfileEntity profileEntity = getProfileEntity(registration);

		setProfileEntityFromAnswers(registration.getAnswers(), profileEntity);

		profileService.updateProfile(profileEntity);
	}

	public void populateRegistrationAnswers(Registration registration)
	{
		ProfileEntity profileEntity = getProfileEntity(registration);

		Simply.logObject(profileEntity, ProfileProcess.class);

		Set<BlockEntity> blockEntities = fetchBlocksForConference(registration.getConferenceId());

		setAnswersFromProfileEntity(registration, blockEntities, profileEntity);
	}

	public Set<BlockEntity> fetchBlocksForConference(UUID conferenceId)
	{
		Set<BlockEntity> blockEntities = new HashSet<BlockEntity>();

		List<PageEntity> pageEntities = pageService.fetchPagesForConference(conferenceId);
		for(PageEntity pageEntity : pageEntities)
			blockEntities.addAll(blockService.fetchBlocksForPage(pageEntity.getId()));

		return blockEntities;
	}

	private ProfileEntity getProfileEntity(Registration registration)
	{
		ProfileEntity profileEntity = profileService.getProfileByUser(registration.getUserId());

		if(profileEntity == null)
		{
			profileEntity = new ProfileEntity(UUID.randomUUID(), registration.getUserId());

			profileService.createProfile(profileEntity);
		}

		return profileEntity;
	}

	private void setProfileEntityFromAnswers(Set<Answer> answers, ProfileEntity profileEntity)
	{
		for (Answer answer : answers)
		{
			BlockEntity blockEntity = blockService.getBlockById(answer.getBlockId());

			if (hasProfileType(blockEntity))
			{
				try
				{
					BlockType blockType = BlockType.fromString(blockEntity.getBlockType());

					// deserialize the json answer using the appropriate block type determined answer object
					if(blockType.isTextQuestion())
					{
						TextQuestion textQuestion = JsonNodeHelper.deserialize(answer.getValue(), TextQuestion.class);
						profileEntity.set(textQuestion, blockEntity.getProfileType());
					}

					else if(blockType.isDateQuestion())
					{
						DateQuestion dateQuestion = JsonNodeHelper.deserialize(answer.getValue(), DateQuestion.class);
						profileEntity.set(dateQuestion, blockEntity.getProfileType());
					}

					else if(blockType.isNameQuestion())
					{
						NameQuestion nameQuestion = JsonNodeHelper.deserialize(answer.getValue(), NameQuestion.class);
						profileEntity.set(nameQuestion);
					}

					else if(blockType.isAddressQuestion())
					{
						AddressQuestion addressQuestion = JsonNodeHelper.deserialize(answer.getValue(), AddressQuestion.class);
						profileEntity.set(addressQuestion);
					}
				}
				catch(Exception e)
				{
					logger.error("Could not capture profile from registration for block type " + blockEntity.getBlockType() +
							" and profile type " + blockEntity.getProfileType(), e);
				}
			}
		}
	}

	private void setAnswersFromProfileEntity(Registration registration, Set<BlockEntity> blockEntities, ProfileEntity profileEntity)
	{
		Set<UUID> blocksWithAnswers = getBlocksWithAnswers(registration.getAnswers());

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
