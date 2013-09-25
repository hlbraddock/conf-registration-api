package org.cru.crs.auth;

public class UnauthorizedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public UnauthorizedException()
	{
	}

	public UnauthorizedException(Throwable cause)
	{
		super(cause);
	}
}
