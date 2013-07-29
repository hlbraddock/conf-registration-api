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

import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Registration;
import org.jboss.resteasy.client.ClientResponse;

@Path("/conferences")
public interface ConferenceResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<List<Conference>> getConferences();
	
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Conference> getConference(@PathParam(value = "conferenceId") UUID conferenceId);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Conference> createConference(Conference conference)throws URISyntaxException;
	
	@SuppressWarnings(value="rawtypes")
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse updateConference(Conference conference, @PathParam(value = "conferenceId") UUID conferenceId);
	
	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Page> createPage(Page newPage, @PathParam(value = "conferenceId") UUID conferenceId) throws URISyntaxException;

	@POST
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> createRegistration(Registration newRegistration, @PathParam(value = "conferenceId") UUID conferenceId);

	@GET
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<List<Registration>> getRegistrations(@PathParam(value = "conferenceId") UUID conferenceId);
}
