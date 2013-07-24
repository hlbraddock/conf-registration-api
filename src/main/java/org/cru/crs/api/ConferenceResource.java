package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Registration;
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
	public Response getConferences()
	{
		return Response.ok(Conference.fromJpa(conferenceService.fetchAllConferences())).build();
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
	 * Creates a new conference.
	 * 
	 * @param conference
	 * @return
	 * @throws URISyntaxException
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createConference(Conference conference)throws URISyntaxException
	{		
		if(conference.getId() == null)
		{
			conference.setId(UUID.randomUUID());
		}
		
		conferenceService.createNewConference(conference.toJpaConferenceEntity());
		
		return Response.created(new URI("/conferences/" + conference.getId())).build();
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
	public Response updateConference(Conference conference, @PathParam(value = "conferenceId") UUID conferenceId)
	{
		/**
		 * First check if the conference IDs are both present, and are different.  If so then throw a 400.
		 */
		if(IdComparer.idsAreNotNullAndDifferent(conferenceId, conference.getId()))
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		/**
		 * Now, if we don't have a conference ID, or we do but it doesn't exist, then create a new conference.
		 */
		if(conferenceId == null)
		{
			conferenceService.createNewConference(conference.toJpaConferenceEntity().setId(UUID.randomUUID()));
		}
		else if(conferenceService.fetchConferenceBy(conferenceId) == null)
		{
			conferenceService.createNewConference(conference.toJpaConferenceEntity().setId(conferenceId));
		}
		
		conferenceService.updateConference(conference.toJpaConferenceEntity().setId(conferenceId));
		
		return Response.noContent().build();
	}
	
	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPage(Page newPage, @PathParam(value = "conferenceId") UUID conferenceId) throws URISyntaxException
	{
		if(newPage.getId() == null) newPage.setId(UUID.randomUUID());
		
		ConferenceEntity conference = conferenceService.fetchConferenceBy(conferenceId);
		
		if(conference == null) return Response.status(Status.BAD_REQUEST).build();
		
		conference.getPages().add(newPage.toJpaPageEntity().setConferenceId(conferenceId));
		
		return Response.created(new URI("/pages/" + newPage.getId())).build();
	}

    @POST
    @Path("/{conferenceId}/registrations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRegistration(Registration newRegistration, @PathParam(value = "conferenceId") UUID conferenceId) throws URISyntaxException
    {
        if(newRegistration.getId() == null) newRegistration.setId(UUID.randomUUID());

        ConferenceEntity conference = conferenceService.fetchConferenceBy(conferenceId);

		logger.info("conference retrieved by id " + conferenceId + " is " + conference);

		if(conference == null) return Response.status(Status.BAD_REQUEST).build();

        RegistrationEntity newRegistrationEntity = newRegistration.toJpaRegistrationEntity(conference);

		registrationService.createNewRegistration(newRegistrationEntity);

		logger.info("new registration created");

		return Response.created(new URI("/registrations/" + newRegistration.getId())).build();
    }

    @GET
    @Path("/{conferenceId}/registrations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistrations(@PathParam(value = "conferenceId") UUID conferenceId) throws URISyntaxException
    {
        return Response.ok(Registration.fromJpa(registrationService.fetchAllRegistrations(conferenceId))).build();
    }
}
