package org.cru.crs.api.client;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.api.model.RegistrationView;
import org.jboss.resteasy.client.ClientResponse;

@Path("/registration-views/{registrationViewId}")
public interface RegistrationViewResourceClient {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<RegistrationView> getRegistrationView(@PathParam(value = "registrationViewId") UUID registrationViewId,
																	@HeaderParam(value = "Authorization") String authCode);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse updateRegistrationView(@PathParam(value = "registrationViewId") UUID registrationViewId,
													@HeaderParam(value = "Authorization") String authCode,
													RegistrationView registrationView);
	
	@DELETE
	public ClientResponse deleteRegistrationView(@PathParam(value = "registrationViewId") UUID registrationViewId,
													@HeaderParam(value = "Authorization") String authCode);
}
