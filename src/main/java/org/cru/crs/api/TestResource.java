package org.cru.crs.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.api.model.errors.BadGateway;
import org.cru.crs.api.model.errors.BadRequest;
import org.cru.crs.api.model.errors.Gone;
import org.cru.crs.api.model.errors.NotFound;
import org.cru.crs.api.model.errors.ServerError;
import org.cru.crs.api.model.errors.Unauthorized;

@Path("/error-testing")
public class TestResource
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/unauthorized")
	public Response unauthorized()
	{
		return Response.ok(new Unauthorized()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/not-found")
	public Response notFound()
	{
		return Response.ok(new NotFound()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/server-error")
	public Response serverError()
	{
		return Response.ok(new ServerError()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bad-gateway")
	public Response badGateway()
	{
		return Response.ok(new BadGateway()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bad-request")
	public Response badRequest()
	{
		return Response.ok(new BadRequest()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/gone")
	public Response gone()
	{
		return Response.ok(new Gone()).build();
	}
	
}
