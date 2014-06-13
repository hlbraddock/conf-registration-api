package org.cru.crs.api.client;

import org.cru.crs.api.model.ProfilePlus;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/profile")
public interface ProfileResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<ProfilePlus> getProfile(@HeaderParam(value = "Authorization") String authCode);
}
