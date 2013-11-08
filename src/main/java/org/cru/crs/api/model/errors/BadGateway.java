package org.cru.crs.api.model.errors;

public class BadGateway extends org.cru.crs.api.model.Error
{
	private static final long serialVersionUID = 1L;
	
	public BadGateway()
	{
		statusCode = 502;
		errorMessage = "Bad Gateway";
	}
}
