package org.cru.crs.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;

@Path("/error-testing")
public class TestResource
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/unauthorized")
	public Response unauthorized()
	{
		throw new UnauthorizedException();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/not-found")
	public Response notFound()
	{
		throw new NotFoundException("resource not found");
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/server-error")
	public Response serverError()
	{
		throw new InternalServerErrorException("server error");
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bad-gateway")
	public Response badGateway()
	{
		throw new WebApplicationException(502);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bad-request")
	public Response badRequest()
	{
		throw new BadRequestException("bad request");
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/gone")
	public Response gone()
	{
		return Response.status(Status.GONE).build();
	}
	
}
