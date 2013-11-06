package org.cru.crs.api.model.errors;

public class BadRequest extends org.cru.crs.api.model.Error
{
	private static final long serialVersionUID = 1L;

	public BadRequest()
	{
		statusCode = 400;
		errorMessage = "Bad Request";
	}
}
