package org.cru.crs.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.model.CrsApplicationUser;

@Path("/profile")
@RequestScoped
public class ProfileResource extends TransactionalResource
{
	@Context HttpServletRequest request;
	@Inject CrsUserService userService;
	
	/*required for Weld*/
	public ProfileResource(){ }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLoggedInUser(@HeaderParam("Authorization") String authCode)
	{
		CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);

		return Response.ok().entity(loggedInUser).build();
	}
}
