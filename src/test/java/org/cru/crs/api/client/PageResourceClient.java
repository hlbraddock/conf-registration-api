package org.cru.crs.api.client;

import java.net.URISyntaxException;

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

public interface PageResourceClient
{
	@GET
	@Path("/{pageId}")
	public Response getPage(@PathParam(value="pageId") String pageId);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPage(PageEntity newPage, @PathParam(value = "conferenceId") String conferenceId) throws URISyntaxException;	
	
	@PUT
	@Path("/{pageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePage(PageEntity page, @PathParam(value="pageId") String pageId);
	
	@DELETE
	@Path("/{pageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePage(PageEntity page, @PathParam(value="pageId") String pageId);
}
