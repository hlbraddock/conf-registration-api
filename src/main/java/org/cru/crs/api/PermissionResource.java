package org.cru.crs.api;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.model.Permission;
import org.cru.crs.api.process.UpdatePermissionProcess;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

@Path("/permissions")
public class PermissionResource extends TransactionalResource {

	@Inject PermissionService permissionService;
	@Inject ConferenceService conferenceService;
	@Inject AuthorizationService authorizationService;
	
	@Inject UpdatePermissionProcess updatePermissionProcess;
	
	@Inject CrsUserService crsUserService;
	
	Logger logger = Logger.getLogger(PermissionResource.class);
	
	@GET
	@Path("/{permissionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPermission(@PathParam(value = "permissionId") UUID permissionId,
			 @HeaderParam(value = "Authorization") String authCode) {
		
		logger.info("get permission entity " + permissionId);
		
		CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);
		
		PermissionEntity permissionEntity = permissionService.getPermissionBy(permissionId);
		
		if(permissionEntity == null) throw new NotFoundException();
		
		authorizationService.authorizeConference(conferenceService.fetchConferenceBy(permissionEntity.getConferenceId()), 
				OperationType.READ, 
				crsLoggedInUser);
		
		Simply.logObject(Permission.fromDb(permissionEntity), PermissionResource.class);
		
		return Response.ok()
						.entity(Permission.fromDb(permissionEntity))
						.build();
	}
	
	@PUT
	@Path("/{permissionId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePermission(@PathParam(value = "permissionId") UUID permissionId,
			 @HeaderParam(value = "Authorization") String authCode,
			 Permission permission) {
		logger.info("updating permission entity " + permissionId + "auth code" + authCode);

		CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);
		
		authorizationService.authorizeConference(conferenceService.fetchConferenceBy(permission.getConferenceId()), 
														OperationType.ADMIN, 
														crsLoggedInUser);
		
		Simply.logObject(permission, PermissionResource.class);
		
		if(IdComparer.idsAreNotNullAndDifferent(permissionId, permission.getId())) {
			throw new BadRequestException();
		}
		
		/*above, we've just checked for the case that both are non-null and different, this assures us
		 * that the value in the path is set in the body in case the body is null*/
		permission.setId(permissionId);
		
		updatePermissionProcess.updatePermission(permission, crsLoggedInUser);
		
		return Response.noContent().build();
	}
	
	@PUT //one could argue @POST for this, and I woudn't put up too much of a fight.
	@Path("/{activationCode}/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptPermission(@PathParam(value = "activationCode") String activationCode,
			 @HeaderParam(value = "Authorization") String authCode)
	{
		logger.info("accepting permission entity with code" + activationCode + "auth code" + authCode);
		
		updatePermissionProcess.acceptPermission(crsUserService.getLoggedInUser(authCode), activationCode);
				
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{permissionId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response revokePermission(@PathParam(value = "permissionId") UUID permissionId,
									 @HeaderParam(value = "Authorization") String authCode) {
		logger.info("revoking permission entity " + permissionId + "auth code" + authCode);
		
		CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);
		PermissionEntity existingPermissionEntity = permissionService.getPermissionBy(permissionId);
		
		/* the permission already doesn't exist, so we're golden*/
		if(existingPermissionEntity == null) return Response.noContent().build();
		
		authorizationService.authorizeConference(conferenceService.fetchConferenceBy(existingPermissionEntity.getConferenceId()), 
														OperationType.ADMIN, 
														crsLoggedInUser);
		
		existingPermissionEntity.setPermissionLevel(PermissionLevel.NONE);
		
		updatePermissionProcess.updatePermission(Permission.fromDb(existingPermissionEntity), crsLoggedInUser);
		
		return Response.noContent().build();
	}
}
