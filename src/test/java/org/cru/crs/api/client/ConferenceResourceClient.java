package org.cru.crs.api.client;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
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
	
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<ConferenceEntity> updateConference(ConferenceEntity conference, @PathParam(value = "conferenceId") String conferenceId);
	
	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPage(PageEntity newPage, @PathParam(value = "conferenceId") String conferenceId) throws URISyntaxException;
}
