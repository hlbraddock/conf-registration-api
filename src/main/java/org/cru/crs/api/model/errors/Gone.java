package org.cru.crs.api.model.errors;

public class Gone extends org.cru.crs.api.model.Error
{
	private static final long serialVersionUID = 1L;

	public Gone()
	{
		statusCode = 410;
		errorMessage = "Resource is gone";
	}
}
