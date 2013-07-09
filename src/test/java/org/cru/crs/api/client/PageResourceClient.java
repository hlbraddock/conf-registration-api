package org.cru.crs.api.client;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cru.crs.model.PageEntity;
import org.jboss.resteasy.client.ClientResponse;

@Path("/pages/{pageId}")
public interface PageResourceClient
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<PageEntity> getPage(@PathParam(value="pageId") UUID pageId);
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<PageEntity> updatePage(PageEntity page, @PathParam(value="pageId") UUID pageId);
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public ClientResponse<PageEntity> deletePage(PageEntity page, @PathParam(value="pageId") UUID pageId);
}
