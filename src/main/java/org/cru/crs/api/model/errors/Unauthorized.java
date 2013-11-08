package org.cru.crs.api.model.errors;

public class Unauthorized extends org.cru.crs.api.model.Error
{
	private static final long serialVersionUID = 1L;

	public Unauthorized()
	{
		this.statusCode = 401;
		this.errorMessage = "Unauthorized";
	}
}
