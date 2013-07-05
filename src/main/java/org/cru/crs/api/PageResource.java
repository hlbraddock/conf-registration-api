package org.cru.crs.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.model.PageEntity;
import org.cru.crs.service.PageService;

import com.google.common.base.Preconditions;

@Path("/conferences/{conferenceId}/pages")
public class PageResource
{
	@Inject EntityManager em;
	
	@GET
	@Path("/{pageId}")
	public Response getPage(@PathParam(value="pageId") String pageId)
	{
		return Response.ok(new PageService(em).fetchPageBy(UUID.fromString(pageId))).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPage(PageEntity newPage, @PathParam(value = "conferenceId") String conferenceId) throws URISyntaxException
	{
		Preconditions.checkState(newPage.getId() == null);

		newPage.setId(UUID.randomUUID());
		
		new PageService(em).createNewPage(newPage);
		
		return Response.created(new URI("/confereneces/" + conferenceId + "/pages/" + newPage.getId())).build();
	}
	
	@PUT
	@Path("/{pageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(PageEntity page, @PathParam(value="pageId") String pageId)
	{
		Preconditions.checkNotNull(page.getId());
		
		new PageService(em).updatePage(page);
				
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{pageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(PageEntity page, @PathParam(value="pageId") String pageId)
	{
		Preconditions.checkNotNull(page.getId());
		
		new PageService(em).deletePage(page);
		
		return Response.ok().build();
	}
}
