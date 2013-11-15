package org.cru.crs.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnauthorizedException;

import com.google.common.base.Throwables;

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

	/**
	 * This code will look at the application exception and look at it's cause to see if it's 
	 * an exception that should return a status code 4xx or 5xx other than 500.  It will
	 * look at the cause of the ApplicationException and continually check up the chain of
	 * exceptions until either we reach the root cause..  
	 */
	@Override
	public Response toResponse(ApplicationException e)
	{
		Throwable cause = e;
		Throwable rootCause = Throwables.getRootCause(e);
		
		while(true)
		{
			if(cause instanceof BadRequestException)
			{
				return Response.status(Status.BAD_REQUEST).entity(cause.getMessage()).build();
			}

			else if(cause instanceof UnauthorizedException)
			{
				return Response.status(Status.UNAUTHORIZED).entity(cause.getMessage()).build();
			}

			else if(cause instanceof NotFoundException)
			{
				return Response.status(Status.NOT_FOUND).entity(cause.getMessage()).build();
			}
			
			/*if we haven't found one of the above types and we're at the root cause, then just agree that it's a server error and move on*/
			else if(cause == rootCause)
			{
				return Response.serverError().entity(e).build();
			}

			//now head up the chain to get the cause's cause
			cause = cause.getCause();
		}
	}

}
