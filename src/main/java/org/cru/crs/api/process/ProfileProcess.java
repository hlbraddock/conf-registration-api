package org.cru.crs.api.process;

import org.ccci.util.strings.Strings;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.domain.ProfileAttribute;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ProfileService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ lee.braddock
 */
public class ProfileProcess
{
	BlockService blockService;
	ProfileService profileService;

	@Inject
	public ProfileProcess(BlockService blockService, ProfileService profileService)
	{
		this.blockService = blockService;
		this.profileService = profileService;
	}

	public void capture(Registration registration)
	{
		ProfileEntity profileEntity = getProfileEntity(registration);

		List<ProfileAttribute> profileAttributes = profileAttributesFromAnswers(registration.getAnswers());

		profileEntity.setProfileAttributes(profileAttributes);

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

	private List<ProfileAttribute> profileAttributesFromAnswers(Set<Answer> answers)
	{
		List<ProfileAttribute> profileAttributes = new ArrayList<ProfileAttribute>();

		for (Answer answer : answers)
		{
			BlockEntity blockEntity = blockService.fetchBlockBy(answer.getBlockId());

			if (hasProfileType(blockEntity))
			{
				profileAttributes.add(new ProfileAttribute(blockEntity.getProfileType(), answer.getValue()));
			}
		}

		return profileAttributes;
	}

	private boolean hasProfileType(BlockEntity blockEntity)
	{
		return blockEntity.getProfileType() != null && !Strings.isEmpty(blockEntity.getProfileType().toString());
	}
}
