package org.cru.crs.api;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.api.model.Block;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;

@Stateless
@Path("/blocks/{blockId}")
public class BlockResource
{
	
	@Inject ConferenceService conferenceService;
	@Inject PageService pageService;
	@Inject BlockService blockService;
	
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
}
