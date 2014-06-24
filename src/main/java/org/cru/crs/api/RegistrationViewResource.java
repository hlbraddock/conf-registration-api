package org.cru.crs.api;

import org.cru.crs.api.model.RegistrationView;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.RegistrationViewEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationViewService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

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
import java.util.UUID;

@Path("/registration-views/{registrationViewId}")
public class RegistrationViewResource extends TransactionalResource {

	@Inject RegistrationViewService registrationViewService;
	@Inject ConferenceService conferenceService;
	@Inject AuthorizationService authorizationService;
	
	@Inject CrsUserService crsUserService;
	
	Logger logger = Logger.getLogger(RegistrationViewResource.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistrationView(@PathParam(value = "registrationViewId") UUID registrationViewId,
			 								@HeaderParam(value = "Authorization") String authCode) {
		
		logger.info("get registration view entity " + registrationViewId + " auth code " + authCode);
		
		RegistrationViewEntity registrationViewEntity = registrationViewService.getRegistrationViewById(registrationViewId);
		
		if(registrationViewEntity == null) throw new NotFoundException();
		
		Simply.logObject(RegistrationView.fromDb(registrationViewEntity), RegistrationViewResource.class);
		
		return Response.ok()
						.entity(RegistrationView.fromDb(registrationViewEntity))
						.build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRegistrationView(@PathParam(value = "registrationViewId") UUID registrationViewId,
											@HeaderParam(value = "Authorization") String authCode,
											RegistrationView registrationView) {
		logger.info("updating registration view entity " + registrationViewId + "auth code" + authCode);

		CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);
		
		authorizationService.authorizeConference(conferenceService.fetchConferenceBy(registrationView.getConferenceId()), 
																	OperationType.READ, 
																	crsLoggedInUser);
		
		Simply.logObject(registrationView, RegistrationViewResource.class);
		
		if(IdComparer.idsAreNotNullAndDifferent(registrationViewId, registrationView.getId())) {
			throw new BadRequestException();
		}
		
		/*above, we've just checked for the case that both are non-null and different, this assures us
		 * that the value in the path is set in the body in case the body is null*/
		registrationView.setId(registrationViewId);
		
		registrationViewService.updateRegistrationView(registrationView.toDbDataViewEntity());
		
		return Response.noContent().build();
	}
	
	@DELETE
	public Response deleteRegistrationView(@PathParam(value = "registrationViewId") UUID registrationViewId,
									 			@HeaderParam(value = "Authorization") String authCode)
	{
		logger.info("revoking registration view entity " + registrationViewId + "auth code" + authCode);
		
		CrsApplicationUser crsLoggedInUser = crsUserService.getLoggedInUser(authCode);
		RegistrationViewEntity existingRegistrationViewEntity = registrationViewService.getRegistrationViewById(registrationViewId);
		
		/* the registration view already doesn't exist, so we're golden*/
		if(existingRegistrationViewEntity == null) return Response.noContent().build();
		
		authorizationService.authorizeConference(conferenceService.fetchConferenceBy(existingRegistrationViewEntity.getConferenceId()), 
																	OperationType.READ, 
																	crsLoggedInUser);
		
		registrationViewService.deleteRegistrationView(existingRegistrationViewEntity.getId());
		
		return Response.noContent().build();
	}
}
