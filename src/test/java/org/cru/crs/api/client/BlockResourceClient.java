package org.cru.crs.api.client;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.model.BlockEntity;
import org.jboss.resteasy.client.ClientResponse;

@Stateless
@Path("/blocks/{blockId}")
public interface BlockResourceClient
{
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public ClientResponse<BlockEntity> getBlock(@PathParam(value="blockId") UUID blockId);
		
		@PUT
		@Consumes(MediaType.APPLICATION_JSON)
		public ClientResponse<BlockEntity> updateBlock(BlockEntity block, @PathParam(value="blockId") UUID blockId);
		
		@DELETE
		@Consumes(MediaType.APPLICATION_JSON)
		public ClientResponse<BlockEntity> deleteBlock(BlockEntity block, @PathParam(value="blockId") UUID blockId);
}
