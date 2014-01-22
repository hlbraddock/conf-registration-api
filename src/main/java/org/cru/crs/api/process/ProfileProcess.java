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
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.ccci.util.strings.Strings;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.utils.JsonUtils;
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

		Set<BlockEntity> blockEntities = fetchBlocksForConference(registration);

		setAnswersFromProfileEntity(registration, blockEntities, profileEntity);
	}

	private Set<BlockEntity> fetchBlocksForConference(Registration registration)
	{
		Set<BlockEntity> blockEntities = new HashSet<BlockEntity>();

		List<PageEntity> pageEntities = pageService.fetchPagesForConference(registration.getConferenceId());
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
					ProfileEntity profileEntityFromAnswer = new ObjectMapper().readValue(answer.getValue(), ProfileEntity.class);

					profileEntity.set(profileEntityFromAnswer);
				}
				catch (Exception e)
				{
					logger.error("Could not set profile entity for type " + blockEntity.getProfileType() + " and value " + answer.getValue());
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
				// build a profile entity having only values pertaining to the profile type specified by the block
				ProfileEntity typeSpecificProfileEntity = profileEntity.typeSpecific(blockEntity.getProfileType());

				// serialize the type specific profile entity into json formatted string
				String jsonString = gson.toJson(typeSpecificProfileEntity);

				// build json node type from json formatted string
				JsonNode jsonNode = JsonUtils.jsonNodeFromString(jsonString);

				// construct the answer
				Answer answer = new Answer(UUID.randomUUID(), registration.getId(), blockEntity.getId(), jsonNode);

				// add answer to the registration
				registration.getAnswers().add(answer);
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

	private boolean hasProfileType(BlockEntity blockEntity)
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
