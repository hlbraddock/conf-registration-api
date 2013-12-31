package org.cru.crs.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ApplicationException;

@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<ApplicationException>
{

	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public Response toResponse(ApplicationException applicationException)
	{
		Throwable actualException = unwrapApplicationException(applicationException);
		
		if(actualException instanceof WebApplicationException)
		{
			return ((WebApplicationException)actualException).getResponse();
		}
		
		log.error("5** exception caught", actualException);
		
		return Response.serverError().header("Error" , actualException.getMessage()).build();
	}

	private Throwable unwrapApplicationException(ApplicationException applicationException)
	{
		return applicationException.getCause();
	}
}
