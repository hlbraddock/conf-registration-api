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
import javax.ws.rs.core.Response.StatusType;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;

@Stateless
@Path("/pages")
public class PageResource
{
	@Inject EntityManager em;
	
	@GET
	@Path("/{pageId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPage(@PathParam(value="pageId") UUID pageId)
	{
		PageEntity requestedPage = new PageService(em).fetchPageBy(pageId);
		
		if(requestedPage == null) return Response.status(Status.NOT_FOUND).build();
		
		return Response.ok(requestedPage).build();
	}
	
	@PUT
	@Path("/{pageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(PageEntity page, @PathParam(value="pageId") UUID pageId)
	{
		Preconditions.checkNotNull(pageId);
		
		PageService pageService = new PageService(em);
		
		if(pageService.fetchPageBy(pageId) == null)
		{
			ConferenceEntity conference = new ConferenceService(em).fetchConferenceBy(page.getConferenceId());
			
			if(conference != null)
			{
				conference.getPages().add(page);
			}
			else
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		else
		{
			pageService.updatePage(page);
		}
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{pageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(PageEntity page, @PathParam(value="pageId") UUID pageId)
	{
		Preconditions.checkNotNull(page.getId());
		
		new PageService(em).deletePage(page);
		
		return Response.ok().build();
	}
}
