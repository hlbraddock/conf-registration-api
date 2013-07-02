package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.optimizer.ConferenceOptimizer;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.service.ConferenceService;

import com.google.common.base.Preconditions;

@Stateless
@Path("/conferences")
public class ConferenceResource
{
	@Inject EntityManager em;
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ConferenceEntity> getConferences()
	{
		List<ConferenceEntity> conferences = new ConferenceService(em).fetchAllConferences();
		for(ConferenceEntity conference : conferences)
		{
			conference = ConferenceOptimizer.removePagesFromConference(conference);
		}
		return conferences;
	}
	
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ConferenceEntity getConference(@PathParam(value = "conferenceId") UUID conferenceId)
	{
		return new ConferenceService(em).fetchConferenceBy(conferenceId);
	}
	
	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createConference(ConferenceEntity conference)throws URISyntaxException
	{
		Preconditions.checkState(conference.getId() == null);
		
		conference.setId(UUID.randomUUID());
		
		new ConferenceService(em).createNewConference(conference);
		
		return Response.created(new URI("/conferences/" + conference.getId())).build();
	}
	
}
