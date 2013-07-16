package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.service.ConferenceService;

@Stateless
@Path("/conferences")
public class ConferenceResource
{
	@Inject EntityManager em;
	
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
		return Response.ok(Conference.fromJpa(new ConferenceService(em).fetchAllConferences())).build();
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
		ConferenceEntity requestedConference = new ConferenceService(em).fetchConferenceBy(conferenceId);
		
		if(requestedConference == null) return Response.status(Status.NOT_FOUND).build();
		
		return Response.ok(Conference.fromJpa(requestedConference)).build();
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
		
		new ConferenceService(em).createNewConference(conference.toJpaConferenceEntity());
		
		return Response.created(new URI("/conferences/" + conference.getId())).build();
	}
	
	/**
	 * Updates an existing conference
	 * 
	 * This PUT will create a new conference resource if the conference specified by @Param conferenceId does
	 * not exist.
	 * 
	 * The Path @Param conference ID is used to lookup whether or not the conference exists or not.
	 * 
	 * @param conference
	 * @return
	 */
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateConference(Conference conference, @PathParam(value = "conferenceId") UUID conferenceId)
	{
		ConferenceService conferenceService = new ConferenceService(em);
		
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
		
		ConferenceEntity conference = new ConferenceService(em).fetchConferenceBy(conferenceId);
		
		if(conference == null) return Response.status(Status.BAD_REQUEST).build();
		
		conference.getPages().add(newPage.toJpaPageEntity().setConferenceId(conferenceId));
		
		return Response.created(new URI("/pages/" + newPage.getId())).build();
	}
}
