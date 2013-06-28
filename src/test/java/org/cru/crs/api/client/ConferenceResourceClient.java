package org.cru.crs.api.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
}
