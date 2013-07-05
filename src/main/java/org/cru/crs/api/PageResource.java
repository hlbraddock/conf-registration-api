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

import org.cru.crs.model.PageEntity;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;

@Stateless
@Path("/pages/{pageId}")
public class PageResource
{
	@Inject EntityManager em;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPage(@PathParam(value="pageId") String pageId)
	{
		return Response.ok(new PageService(em).fetchPageBy(UUID.fromString(pageId))).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(PageEntity page, @PathParam(value="pageId") String pageId)
	{
		Preconditions.checkNotNull(page.getId());
		
		new PageService(em).updatePage(page);
				
		return Response.noContent().build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(PageEntity page, @PathParam(value="pageId") String pageId)
	{
		Preconditions.checkNotNull(page.getId());
		
		new PageService(em).deletePage(page);
		
		return Response.ok().build();
	}
}
