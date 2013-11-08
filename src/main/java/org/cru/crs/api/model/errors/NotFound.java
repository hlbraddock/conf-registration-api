package org.cru.crs.api.model.errors;

public class NotFound extends org.cru.crs.api.model.Error
{
	private static final long serialVersionUID = 1L;

	public NotFound()
	{
		statusCode = 404;
		errorMessage = "Resource not found";
	}
}
