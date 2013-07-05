package org.cru.crs.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/conferences/{conferenceId}/pages")
public class PageResource
{

	@POST
	public Response createPage()
	{
		
		return Response.created(null).build();
	}
	
	@PUT
	public Response updatePage()
	{
		
		return Response.noContent().build();
	}
	
	@DELETE
	public Response deletePage()
	{
	
		return Response.ok().build();
	}
}
