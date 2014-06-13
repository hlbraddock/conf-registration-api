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

import org.cru.crs.api.model.Profile;
import org.cru.crs.api.model.ProfilePlus;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.service.ProfileService;
import org.cru.crs.utils.Simply;
import org.jboss.logging.Logger;

@Path("/profile")
@RequestScoped
public class ProfileResource extends TransactionalResource
{
	@Context HttpServletRequest request;
	@Inject CrsUserService userService;
	@Inject ProfileService profileService;

	/*required for Weld*/
	public ProfileResource(){ }

	Logger logger = Logger.getLogger(getClass());

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@HeaderParam("Authorization") String authCode)
	{
		CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);

		ProfileEntity profileEntity = profileService.getProfileByUser(loggedInUser.getId());

		ProfilePlus profilePlus = new ProfilePlus(Profile.fromDb(profileEntity), loggedInUser.getAuthProviderType());

		logger.info("profile auth provider " + profilePlus.getAuthProviderType());

		Simply.logObject(profilePlus, getClass());

		return Response.ok().entity(profilePlus).build();
	}
}
