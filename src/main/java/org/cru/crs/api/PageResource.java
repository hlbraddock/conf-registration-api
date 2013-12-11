package org.cru.crs.api;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.model.Page;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;

@Path("/pages/{pageId}")
public class PageResource extends TransactionalResource
{
	@Inject PageService pageService;
    @Inject ConferenceService conferenceService;
    
    @Context HttpServletRequest request;
    @Inject CrsUserService userService;
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPage(@PathParam(value="pageId") UUID pageId)
	{
		PageEntity requestedPage = pageService.fetchPageBy(pageId);

		if(requestedPage == null)
		{
			throw new NotFoundException("Requested page was not found");
		}

		return Response.ok(Page.fromDb(requestedPage)).build();
	}
}
