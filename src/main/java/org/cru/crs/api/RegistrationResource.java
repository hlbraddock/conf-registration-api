package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.RegistrationFetchProcess;
import org.cru.crs.api.process.RegistrationUpdateProcess;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.BadRequestException;

@Path("/registrations/{registrationId}")
public class RegistrationResource
{
	@Inject RegistrationService registrationService;
	@Inject ConferenceService conferenceService;
	@Inject CrsUserService userService;
    @Inject AnswerService answerService;
    
    @Inject AuthorizationService authorizationService;
    
    @Inject RegistrationFetchProcess registrationFetchProcess;
    @Inject RegistrationUpdateProcess registrationUpdateProcess;
    
    @Inject Clock clock; 
        
	@Context HttpServletRequest request;

	private Logger logger = Logger.getLogger(RegistrationResource.class);

	/**
	 * Returns a registration resource specified by @param registrationId
	 * 
	 * Possible Outcomes:
	 * 	200 Ok - found registration and the user specified by @param authCode has read access
	 *  404 Not Found - no registration resource specified by @param registrationId
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have read access to this registration.
	 *  
	 * @param authCode
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode)
	{
		logger.info("get registration entity " + registrationId + " and auth code " + authCode);

		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		Registration registration = registrationFetchProcess.get(registrationId);

		if(registration == null)
		{
			throw new NotFoundException("Registration: " + registrationId + " was not found.");
		}

		authorizationService.authorize(registration.toDbRegistrationEntity(), 
				conferenceService.fetchConferenceBy(registration.getConferenceId()), 
				OperationType.READ,
				crsLoggedInUser);

		logger.info("get registration");
		Simply.logObject(registration, RegistrationResource.class);

		return Response.ok(registration).build();
	}

	/**
	 * Updates registration resource specified by @param registrationId
	 * 
	 * Possible Outcomes:
	 * 	204 No Content - registration specified by @param registrationId was found and updated. user specified by @param authCode has update access.
	 *  201 Created - registration specified by @param registrationId was not found but user specified by @param authCode has create access.  registration created.
	 *  400 Bad Request - path and entity registration id do not match or were null, or the conference the updated registation should belong to doesn't exist
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have create and/or update access to this registration.
	 *  502 Bad Gateway - registration specified by @param registrationId was updated, but the payment was not successfully processed.
	 *  
	 * @param authCode
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateRegistration(Registration registration, @PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		logger.info("update registration entity " + registrationId + " and auth code " + authCode);

		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		/*If the path registration id and the entity's registration id are both not null and different, then this is a bad request.
		 * Malicious or not, we don't really know what the user wants to do.*/
		if(IdComparer.idsAreNotNullAndDifferent(registrationId, registration.getId()) || registration.getId() == null || registrationId == null)
		{
			throw new BadRequestException("The path registration id: " + registrationId + " and entity registration id: " + registration.getId() + " were either null or don't match");
		}

		/*Find the conference that this registration is for.  If we can't find it, then this is a bad request*/
		ConferenceEntity conferenceEntityForUpdatedRegistration = conferenceService.fetchConferenceBy(registration.getConferenceId());

		if(conferenceEntityForUpdatedRegistration == null)
		{
			throw new BadRequestException("The conference this registration should belong to does not exist");
		}

		logger.info("update registration");
		Simply.logObject(registration, RegistrationResource.class);

		boolean createdNewRegistration = false;

		/*creates on the update endpoint are supported, so if the registration here doesn't exist it will be created, otherwise
		 * it will be updated*/
		if(registrationService.getRegistrationBy(registrationId) == null)
		{
			createdNewRegistration = true;

			RegistrationEntity registrationEntity = registration.toDbRegistrationEntity();

			logger.info("update registration creating");

			authorizationService.authorize(registrationEntity, conferenceEntityForUpdatedRegistration, OperationType.CREATE, crsLoggedInUser);

			registrationUpdateProcess.performDeepUpdate(registration);
			
			if(createdNewRegistration)
			{
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}

			/*save the registration to the DB*/
			registrationService.createNewRegistration(registrationEntity);
		}

		authorizationService.authorize(registration.toDbRegistrationEntity(), conferenceEntityForUpdatedRegistration, OperationType.UPDATE, crsLoggedInUser);

		registrationUpdateProcess.performDeepUpdate(registration);

		if(createdNewRegistration)
		{
			return Response.status(Status.CREATED)
					.location(new URI("/registrations/" + registration.getId()))
					.entity(registration)
					.build();
		}
		else return Response.noContent().build();
	}
	
	/**
	 * Deletes registration resource specified by @param registrationId
	 * 
	 * Possible Outcomes:
	 * 	204 No Content - registration specified by @param registrationId was found and deleted. user specified by @param authCode has delete access.
	 *  400 Bad Request - registration specified by @param registrationId was not found.
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have delete access to this registration.
	 *  
	 * @param authCode
	 * @return
	 */
	@DELETE
	public Response deleteRegistration(@PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode)
	{
		logger.info("delete registration entity " + registrationId + " and auth code " + authCode);

		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		RegistrationEntity registrationEntity = registrationService.getRegistrationBy(registrationId);

		if(registrationEntity == null)
		{
			throw new BadRequestException("The registration being deleted does not exist");
		}

		Simply.logObject(Registration.fromDb(registrationEntity), RegistrationResource.class);

		authorizationService.authorize(registrationEntity, 
				conferenceService.fetchConferenceBy(registrationEntity.getConferenceId()), 
				OperationType.DELETE, 
				crsLoggedInUser);

		registrationService.deleteRegistration(registrationEntity);

		return Response.noContent().build();
	}

	/**
	 * Creates a new answer resource and associates it to registration specified by @param registrationId
	 * 
	 * Possible Outcomes:
	 *  201 Created - @param answer was created and associated to registration specified by @param registrationId
	 *  400 Bad Request - path and entity registration id do not match or were null, or the registration the new answer should belong to doesn't exist
	 *  401 Unauthorized - user specified by @param authCode is expired, doesn't exist or doesn't have update access to this registration.
	 *  
	 * @param authCode
	 * @return
	 */
	@POST
	@Path("/answers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAnswer(Answer newAnswer, @PathParam(value = "registrationId") UUID registrationId, @HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		logger.info("create answer entity " + registrationId + " and auth code " + authCode);

		CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

		/*if the path registration id and the entity's registration id are both not null and different, then this is a bad request
		 * malicious or not, we don't really know what they want to do.*/
		if(IdComparer.idsAreNotNullAndDifferent(registrationId, newAnswer.getRegistrationId()))
		{
			throw new BadRequestException("The path registration id: " + registrationId + " and entity registration id: " + newAnswer.getRegistrationId() + " were either null or don't match");
		}

		/*go find the registration for the new answer.  if it doesn't exist, then this is a bad request*/
		RegistrationEntity registrationEntityForNewAnswer = registrationService.getRegistrationBy(registrationId);

		if(registrationEntityForNewAnswer == null) throw new BadRequestException("The registration for this answer does not exist");

		/*prep the new answer by ensuring it has an ID set, and that it's registration id is set to the registration id specified
		 * in the path.  we've already asserted above that the path and entity registraiton id are the same.*/
		if(newAnswer.getId() == null) newAnswer.setId(UUID.randomUUID());
		if(newAnswer.getRegistrationId() == null) newAnswer.setRegistrationId(registrationId);

		Simply.logObject(newAnswer, RegistrationResource.class);

		authorizationService.authorize(registrationEntityForNewAnswer, conferenceService.fetchConferenceBy(registrationEntityForNewAnswer.getConferenceId()), OperationType.UPDATE, crsLoggedInUser);
		answerService.insertAnswer(newAnswer.toDbAnswerEntity());

		return Response.status(Status.CREATED)
				.entity(newAnswer)
				.header("location", new URI("/answers/" + newAnswer.getId()))
				.build();
	}
}
