package org.cru.crs.api;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;

@Stateless
@Path("/blocks/{blockId}")
public class BlockResource
{
	@Inject EntityManager em;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPage(@PathParam(value="blockId") UUID blockId)
	{
		return null;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(BlockEntity block, @PathParam(value="blockId") UUID blockId)
	{
		Preconditions.checkNotNull(block.getId());
		
				
		return Response.noContent().build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(BlockEntity block, @PathParam(value="blockId") UUID blockId)
	{
		Preconditions.checkNotNull(block.getId());
		
		
		return Response.ok().build();
	}
}
