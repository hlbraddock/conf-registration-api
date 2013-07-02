package org.cru.crs.api.client;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.model.ConferenceEntity;
import org.jboss.resteasy.client.ClientResponse;

@Path("/conferences")
public interface ConferenceResourceClient
{
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<List<ConferenceEntity>> getConferences();
	
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<ConferenceEntity> getConference(@PathParam(value = "conferenceId") UUID conferenceId);
	
	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<ConferenceEntity> createConference(ConferenceEntity conference)throws URISyntaxException;
}
