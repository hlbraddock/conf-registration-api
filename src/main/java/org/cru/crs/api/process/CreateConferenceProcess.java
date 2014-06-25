package org.cru.crs.api.process;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.model.UserEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.UserService;

import javax.inject.Inject;
import java.util.UUID;

public class CreateConferenceProcess
{
	ConferenceService conferenceService;
	UserService userService;
	PermissionService permissionService;
	Clock clock;
		
	@Inject
	public CreateConferenceProcess(ConferenceService conferenceService, UserService userService, PermissionService permissionService, Clock clock)
	{
		this.conferenceService = conferenceService;
		this.userService = userService;
		this.permissionService = permissionService;
		this.clock = clock;
	}

	public void saveNewConference(Conference newConference, CrsApplicationUser loggedInUser)
	{
		if(newConference.getId() == null)
		{
			newConference.setId(UUID.randomUUID());
		}
		
		setInitialContactPersonDetailsBasedOn(newConference, loggedInUser);
		
		conferenceService.createNewConference(newConference.toDbConferenceEntity(),newConference.toDbConferenceCostsEntity());
		
		saveInitialCreatorPermissionForConference(newConference, loggedInUser);
	}

	private Conference setInitialContactPersonDetailsBasedOn(Conference newConference, CrsApplicationUser loggedInUser)
	{
		UserEntity user = userService.getUserById(loggedInUser.getId());
		if(user != null)
		{
			newConference.setContactPersonName(user.getFirstName() + " " + user.getLastName());
			newConference.setContactPersonEmail(user.getEmailAddress());
			newConference.setContactPersonPhone(user.getPhoneNumber());
		}
		return newConference;
	}
	
	private void saveInitialCreatorPermissionForConference(Conference newConference, CrsApplicationUser loggedInUser)
	{
		permissionService.insertPermission(new Permission().setId(UUID.randomUUID())
														.setConferenceId(newConference.getId())
														.setUserId(loggedInUser.getId())
														.setPermissionLevel(PermissionLevel.CREATOR)
														.setTimestamp(clock.currentDateTime())
														.setEmailAddress(loggedInUser.getAuthProviderUsername())
														.toDbPermissionEntity());
	}
}
