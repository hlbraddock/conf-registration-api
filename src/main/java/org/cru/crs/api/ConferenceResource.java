package org.cru.crs.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.jboss.logging.Logger;

@Stateless
@Path("/conferences")
public class ConferenceResource
{
	@Inject ConferenceService conferenceService;
	@Inject RegistrationService registrationService;

	@Context HttpServletRequest request;
	@Inject CrsUserService userService;

	Logger logger = Logger.getLogger(ConferenceResource.class);

	/**
	 * Desired design: Gets all the conferences for which the authenticated user has access to.
	 * 
	 * Current status: Returns all conferences in the system.  Need authentication built out to
	 * implement as designed.
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConferences(@HeaderParam(value="Authentication") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			return Response.ok(Conference.fromJpa(conferenceService.fetchAllConferences(loggedInUser))).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	/**
	 * Return the conference identified by ID with all associated Pages and Blocks
	 * 
	 * @param conferenceId
	 * @return
	 */
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConference(@PathParam(value = "conferenceId") UUID conferenceId)
	{
		ConferenceEntity requestedConference = conferenceService.fetchConferenceBy(conferenceId);

		if(requestedConference == null) return Response.status(Status.NOT_FOUND).build();

		return Response.ok(Conference.fromJpaWithPages(requestedConference)).build();
	}

	/**
	 * Creates a new conference.  In order to create a conference the user must be logged in
	 * with a known identity.  To check this, we look in the session for an appUserId.
	 * 
	 * @param conference
	 * @return
	 * @throws URISyntaxException
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createConference(Conference conference, 
			@HeaderParam(value="Authentication") String authCode)throws URISyntaxException
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			/*if there is no id in the conference, then create one. the client has the ability, but not 
			 * the obligation to create one*/
			if(conference.getId() == null)
			{
				conference.setId(UUID.randomUUID());
			}

			/*persist the new conference*/
			conferenceService.createNewConference(conference.toJpaConferenceEntity(), loggedInUser);
			
			/*return a response with status 201 - Created and a location header to fetch the conference.
			 * a copy of the entity is also returned.*/
			return Response.status(Status.CREATED)
					.location(new URI("/conferences/" + conference.getId()))
					.entity(conference).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	/**
	 * Updates an existing conference
	 * 
	 * This PUT will create a new conference resource if the conference specified by @Param conferenceId does
	 * not exist.
	 * 
	 * If the conference ID path parameter and the conference ID body parameter (in the JSON object) do not match
	 * then this method will fail-fast and return a 400-Bad Request response.
	 * 
	 * @param conference
	 * @return
	 */
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateConference(Conference conference, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value="Authentication") String authCode)
	{

		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			/*Check if the conference IDs are both present, and are different.  If so then throw a 400 - Bad Request*/
			if(IdComparer.idsAreNotNullAndDifferent(conferenceId, conference.getId()))
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			if(conferenceId == null)
			{
				/* If a conference ID wasn't passed in, then create a new conference.*/
				conferenceService.createNewConference(conference.toJpaConferenceEntity().setId(UUID.randomUUID()), 
						loggedInUser);
			}
			else if(conferenceService.fetchConferenceBy(conferenceId) == null)
			{
				/* If the conference id was passed in, but there's no conference associated with it, then create a new conference*/ 
				conferenceService.createNewConference(conference.toJpaConferenceEntity().setId(conferenceId), 
						loggedInUser);
			}
			else
			{
				/*there is an existing conference, so go update it*/
				conferenceService.updateConference(conference.toJpaConferenceEntity().setId(conferenceId), 
						loggedInUser);
				
				
			}
			
			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPage(Page newPage, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authentication") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);
			final ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(conferenceId);

			/*if there is no conference identified by the passed in id, then return a 400 - bad request*/
			if(conferencePageBelongsTo == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			conferenceService.addPageToConference(conferencePageBelongsTo, newPage.toJpaPageEntity(), loggedInUser);
			
			return Response.status(Status.CREATED)
					.location(new URI("/pages/" + newPage.getId()))
					.entity(newPage)
					.build();
		} 
		catch (UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRegistration(Registration newRegistration, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "authCode") String authCode) throws URISyntaxException
	{
		if(newRegistration.getId() == null) newRegistration.setId(UUID.randomUUID());

		logger.info(conferenceId);

		ConferenceEntity conference = conferenceService.fetchConferenceBy(conferenceId);

		logObject(conference, logger);

		if(conference == null) return Response.status(Status.BAD_REQUEST).build();

		RegistrationEntity newRegistrationEntity = newRegistration.toJpaRegistrationEntity(conference);

		logObject(newRegistrationEntity, logger);

		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);
			newRegistrationEntity.setUserId(loggedInUser.getId());
			
			registrationService.createNewRegistration(newRegistrationEntity);

			return Response.status(Status.CREATED)
					.location(new URI("/pages/" + newRegistration.getId()))
					.entity(newRegistration)
					.build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@GET
	@Path("/{conferenceId}/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistrations(@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authentication") String authCode) throws URISyntaxException
	{
		logger.info(conferenceId);

		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);
			Set<RegistrationEntity> registrationEntitySet = registrationService.fetchAllRegistrations(conferenceId, loggedInUser);

			Set<Registration> registrationSet = Registration.fromJpa(registrationEntitySet);

			logObject(registrationSet, logger);
			
			return Response.ok(registrationSet).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@GET
	@Path("/{conferenceId}/registrations/current")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentRegistration(@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "authCode") String authCode) throws URISyntaxException
	{
		logger.info(conferenceId);
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			RegistrationEntity registrationEntity = registrationService.getRegistrationByConferenceIdUserId(conferenceId, loggedInUser.getId());

			Registration registration = Registration.fromJpa(registrationEntity);

			logObject(registration, logger);

			return Response.ok(registration).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build(); 
		}
	}

	private void logObject(Object object, Logger logger)
	{
		try
		{
			logger.info(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(object));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
