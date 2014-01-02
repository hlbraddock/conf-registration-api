package org.cru.crs.api.process;

import org.ccci.util.strings.Strings;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ProfileService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.Set;

/**
 * @ lee.braddock
 */
public class ProfileProcess
{
	private BlockService blockService;
	private ProfileService profileService;

	Logger logger = Logger.getLogger(ProfileProcess.class);

	@Inject
	public ProfileProcess(BlockService blockService, ProfileService profileService)
	{
		this.blockService = blockService;
		this.profileService = profileService;
	}

	public void capture(Registration registration)
	{
		ProfileEntity profileEntity = getProfileEntity(registration);

		setProfileEntityFromAnswers(registration.getAnswers(), profileEntity);

		profileService.updateProfile(profileEntity);
	}

	private ProfileEntity getProfileEntity(Registration registration)
	{
		ProfileEntity profileEntity = profileService.getProfileByUser(registration.getUserId());

		if(profileEntity == null)
		{
			profileEntity = new ProfileEntity(registration.getUserId());

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

	private boolean hasProfileType(BlockEntity blockEntity)
	{
		return blockEntity.getProfileType() != null && !Strings.isEmpty(blockEntity.getProfileType().toString());
	}
}
