package org.cru.crs.api;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.api.model.Block;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.utils.IdComparer;

@Stateless
@Path("/blocks/{blockId}")
public class BlockResource
{
	@Inject BlockService blockService;
	@Inject PageService pageService;
	@Inject ConferenceService conferenceService;

	@Context HttpServletRequest request;
	@Inject CrsUserService userService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBlock(@PathParam(value="blockId") UUID blockId)
	{
		BlockEntity requestedBlock = blockService.fetchBlockBy(blockId);

		if(requestedBlock == null) return Response.status(Status.NOT_FOUND).build();

		return Response.ok(Block.fromJpa(requestedBlock)).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBlock(Block block, 
			@PathParam(value="blockId") UUID blockId,
			@HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(),authCode);

			PageEntity pageBlockBelongsTo = pageService.fetchPageBy(block.getPageId());

			/**
			 * If the Path pageId does not match the pageId in the body of the JSON object,
			 * then fail fast and return a 400.  
			 */
			if(IdComparer.idsAreNotNullAndDifferent(blockId, block.getId()))
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			/**
			 * Now that we know that the blockIds are not different.. take the first not-null
			 * one we find and treat it as the "official" block ID.  Note in this case it still
			 * could be null at this point, and we would need to create a new block.
			 */
			UUID officialBlockId = blockId != null ? blockId : block.getId(); 

			if(officialBlockId == null || pageService.fetchPageBy(officialBlockId) == null)
			{
				/**
				 * If the client didn't specify an ID for the new page, we'll create one
				 */
				if(officialBlockId == null) officialBlockId = UUID.randomUUID();

				/**
				 * Make sure the Page ID is set in the object, it could be that it was only
				 * specified as a path parameter.
				 */
				pageService.addBlockToPage(pageBlockBelongsTo, block.toJpaBlockEntity(), loggedInUser);
			}
			else
			{
				blockService.updateBlock(block.toJpaBlockEntity().setId(officialBlockId), loggedInUser);
			}
			
			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBlock(@PathParam(value="blockId") UUID blockId,
			@HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getUserFromSession(request.getSession(), authCode);

			if(blockId == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			blockService.deleteBlock(blockId, loggedInUser);
			
			return Response.ok().build();
		} 
		catch (UnauthorizedException e) 
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}
}
