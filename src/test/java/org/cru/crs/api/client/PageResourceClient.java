package org.cru.crs.api.client;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.api.model.Page;
import org.jboss.resteasy.client.ClientResponse;

@Path("/pages/{pageId}")
public interface PageResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<Page> getPage(@PathParam(value="pageId") UUID pageId);
	
	@SuppressWarnings(value="rawtypes")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse updatePage(Page page, @PathParam(value="pageId") UUID pageId, @HeaderParam(value = "Authorization") String authCode);
	
	@SuppressWarnings(value="rawtypes")
	@DELETE
	public ClientResponse deletePage(@PathParam(value="pageId") UUID pageId, @HeaderParam(value = "Authorization") String authCode);
}
