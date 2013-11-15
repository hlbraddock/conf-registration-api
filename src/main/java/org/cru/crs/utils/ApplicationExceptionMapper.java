package org.cru.crs.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;

/**
 * IMO RestEasy stupidly wraps all application code exceptions in an instance InternalServerErrorExcpeion
 * All InternalServerErrorExcpeion will return a 500 Server Error. This wrapping is nice for things like NPE's 
 * and the like, but it stinks when I want to throw a RestEasy exception like BadRequestException to return 
 * a status code 400 Bad Request.  The fact that it's wrapped will return it to the client as a 500.
 * 
 *  The alternative to this mapper is to write a bunch of extra code in the resource methods to try and catch
 *  exceptions and return the appropriate response.  That's pretty lame, and lots of code.  This mapper will
 *  check for certain nested exceptions and return the right code to the client.
 * 
 * @author ryancarlson
 *
 */
@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException>
{

	@Override
	public Response toResponse(ApplicationException e)
	{
		//Not sure if this is possible as RestEasy claims to wrap all exceptions but might as well check for safety.
		if(e.getCause() == null) return Response.serverError().entity(e).build();
		
		//First get the internalServerError out
		Throwable internalServerErrorException = e.getCause();
		
		if(internalServerErrorException.getCause() == null) return Response.serverError().entity(internalServerErrorException).build();
		
		else if(internalServerErrorException.getCause() instanceof BadRequestException)
		{
			return Response.status(Status.BAD_REQUEST).entity(internalServerErrorException.getCause().getMessage()).build();
		}

		else if(internalServerErrorException.getCause() instanceof UnauthorizedException)
		{
			return Response.status(Status.UNAUTHORIZED).entity(internalServerErrorException.getCause().getMessage()).build();
		}
		
		else if(internalServerErrorException.getCause() instanceof NotFoundException)
		{
			return Response.status(Status.NOT_FOUND).entity(internalServerErrorException.getCause().getMessage()).build();
		}

		//if it's anything else, just treat it as a server error.
		else return Response.serverError().entity(internalServerErrorException.getCause().getMessage()).build();
 
	}

}
