package org.cru.crs.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ApplicationException;

@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<ApplicationException>
{

	@Override
	public Response toResponse(ApplicationException exception)
	{
		if(exception.getCause() instanceof WebApplicationException)
		{
			return ((WebApplicationException)exception.getCause()).getResponse();
		}
		
		return Response.serverError().header("Error" , exception.getMessage()).entity(exception.getStackTrace()).build();
	}

}
