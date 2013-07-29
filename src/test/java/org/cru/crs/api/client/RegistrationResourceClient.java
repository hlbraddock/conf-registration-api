package org.cru.crs.api.client;

import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/registrations/{registrationId}")
public interface RegistrationResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> getRegistration(@PathParam(value = "registrationId") UUID registrationId);
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> updateRegistration(Registration registration, @PathParam(value = "registrationId") UUID registrationId);
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<Registration> deleteRegistration(Registration registration, @PathParam(value = "registrationId") UUID registrationId);

	@POST
	@Path("/answers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Answer> createAnswer(Answer newAnswer, @PathParam(value = "registrationId") UUID conferenceId);
}
