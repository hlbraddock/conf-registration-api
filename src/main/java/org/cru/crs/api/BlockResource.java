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
import javax.ws.rs.core.Response.Status;

import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;

@Stateless
@Path("/blocks/{blockId}")
public class BlockResource
{

    @Inject BlockService blockService;

    @Inject PageService pageService;


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBlock(@PathParam(value="blockId") UUID blockId)
	{
		BlockEntity requestedBlock = blockService.getBlockBy(blockId);
		
		if(requestedBlock == null) return Response.status(Status.NOT_FOUND).build();
		
		return Response.ok(requestedBlock).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBlock(BlockEntity block, @PathParam(value="blockId") UUID blockId)
	{
		Preconditions.checkNotNull(blockId);
		
		if(blockService.getBlockBy(blockId) == null)
		{
			PageEntity page = pageService.fetchPageBy(block.getPageId());
			
			if(page != null)
			{
				page.getBlocks().add(block);
			}
			else
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
				
		return Response.noContent().build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBlock(BlockEntity block, @PathParam(value="blockId") UUID blockId)
	{
		Preconditions.checkNotNull(block.getId());

        blockService.deleteBlock(block);
		
		return Response.ok().build();
	}
}
