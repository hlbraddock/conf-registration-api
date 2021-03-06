package org.cru.crs.api.client;

import org.cru.crs.api.model.Block;
import org.jboss.resteasy.client.ClientResponse;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Stateless
@Path("/blocks/{blockId}")
public interface BlockResourceClient
{
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public ClientResponse<Block> getBlock(@PathParam(value="blockId") UUID blockId);
}
