package org.cru.crs.api.process;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Permission;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.MailService;

public class CreatePermissionProcess
{
	PermissionService permissionService;
	ConferenceService conferenceService;
	UserService userService;
	MailService mailService;
	CrsProperties properties;
	Clock clock;
	
	@Inject
	public CreatePermissionProcess(PermissionService permissionService,
			ConferenceService conferenceService, UserService userService,
			MailService mailService, CrsProperties properties, Clock clock)
	{
		this.permissionService = permissionService;
		this.conferenceService = conferenceService;
		this.userService = userService;
		this.mailService = mailService;
		this.properties = properties;
		this.clock = clock;
	}
	
	public void savePermission(Permission newPermission, UUID conferenceId, CrsApplicationUser permissionGrantor)
	{
		if(newPermission.getId() == null) newPermission.withRandomID();
		
		permissionService.insertPermission(newPermission.setConferenceId(conferenceId)
														.setActivationCode(AuthCodeGenerator.generate())
														.setTimestamp(clock.currentDateTime())
														.setGivenByUserId(permissionGrantor.getId())
														.toDbPermissionEntity());

	}
	


	public void notifyRecipientOfGrantedPermission(Permission newPermission) throws MalformedURLException, MessagingException
	{
		mailService.send("crs.dev@cru.org", 
							newPermission.getEmailAddress(), 
							PermissionEmail.subject(), 
							PermissionEmail.body(createActivationUrl(newPermission), 
													newPermission.getPermissionLevel(),
													conferenceService.fetchConferenceBy(newPermission.getConferenceId()),
													userService.getUserById(newPermission.getGivenByUserId())));
	}

	private URL createActivationUrl(Permission newPermission) throws MalformedURLException
	{
		return new URL(properties.getProperty("permissionActivationUrl") + "/" +  newPermission.getActivationCode());
	}

}
