package org.cru.crs.api.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.inject.Inject;
import java.lang.reflect.Type;
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
			BlockEntity blockEntity = blockService.fetchBlockBy(answer.getBlockId());

			if (hasProfileType(blockEntity))
			{
				try
				{
					BlockType blockType = BlockType.fromString(blockEntity.getBlockType());

					if(blockType.isTextQuestion())
					{
						TextQuestion textQuestion = gson.fromJson(answer.getValue().toString(), TextQuestion.class);
						profileEntity.set(textQuestion, blockEntity.getProfileType());
					}

					else if(blockType.isDateQuestion())
					{
						DateQuestion dateQuestion = gson.fromJson(answer.getValue().toString(), DateQuestion.class);
						profileEntity.set(dateQuestion, blockEntity.getProfileType());
					}

					else if(blockType.isNameQuestion())
					{
						NameQuestion nameQuestion = gson.fromJson(answer.getValue().toString(), NameQuestion.class);
						profileEntity.set(nameQuestion);
					}

					else if(blockType.isAddressQuestion())
					{
						AddressQuestion addressQuestion = gson.fromJson(answer.getValue().toString(), AddressQuestion.class);
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
					String jsonString = null;

					BlockType blockType = BlockType.fromString(blockEntity.getBlockType());

					// serialize the appropriate block type java object into a json formatted string
					if(blockType.isTextQuestion())
					{
						TextQuestion textQuestion = profileEntity.getTextQuestion(blockEntity.getProfileType());
						if(!textQuestion.isEmpty())
							jsonString = gson.toJson(textQuestion);
					}

					else if(blockType.isDateQuestion())
					{
						DateQuestion dateQuestion = profileEntity.getDateQuestion(blockEntity.getProfileType());
						if(!dateQuestion.isEmpty())
							jsonString = gson.toJson(dateQuestion);
					}

					else if(blockType.isNameQuestion())
					{
						NameQuestion nameQuestion = profileEntity.getNameQuestion();
						if(!nameQuestion.isEmpty())
							jsonString = gson.toJson(nameQuestion);
					}

					else if(blockType.isAddressQuestion())
					{
						AddressQuestion addressQuestion = profileEntity.getAddressQuestion();
						if(addressQuestion.isEmpty())
							jsonString = gson.toJson(addressQuestion);
					}

					if(Strings.isEmpty(jsonString))
						return;

					// build json node type from json formatted string
					JsonNode jsonNode = JsonNodeHelper.toJsonNode(jsonString);

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
			BlockEntity blockEntity = blockService.fetchBlockBy(answer.getBlockId());

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

	private Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

	private class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
		// No need for an InstanceCreator since DateTime provides a no-args constructor
		@Override
		public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString(DateTimeFormat.fullDateTime()));
		}
		@Override
		public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
				throws JsonParseException {
			return new DateTime(json.getAsString());
		}
	}
}
