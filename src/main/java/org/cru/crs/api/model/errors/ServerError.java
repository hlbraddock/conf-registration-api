package org.cru.crs.api.model.errors;

public class ServerError extends org.cru.crs.api.model.Error
{
	private static final long serialVersionUID = 1L;
	
	public ServerError()
	{
		statusCode = 500;
		errorMessage = "Server error";
	}
	
	public ServerError(Throwable t)
	{
		this();
		setCustomErrorMessage(t.getMessage());
	}
}
