package org.cru.crs.jaxrs;

import javax.ws.rs.ClientErrorException;

/**
 * Created this class b/c RESTEasy overlooked it.
 * 
 * @author ryancarlson
 *
 */
public class UnauthorizedException extends ClientErrorException
{
	private static final long serialVersionUID = 1L;

	public UnauthorizedException()
	{
		super(401);
	}

}
