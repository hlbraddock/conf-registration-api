package org.cru.crs.api;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;

@Path("/profile")
public class ProfileResource
{
	@Context HttpServletRequest request;
	@Inject CrsUserService userService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLoggedInUser(@HeaderParam("Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);
			
			return Response.ok().entity(loggedInUser).build();
		} 
		catch (UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
	}
}
