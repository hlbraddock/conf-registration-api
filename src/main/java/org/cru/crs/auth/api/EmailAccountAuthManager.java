package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.service.IdentityService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;

@Stateless
@Path("/auth/email")
public class EmailAccountAuthManager
{
	@Inject
	CrsProperties crsProperties;

	@Inject IdentityService authenticationProviderService;
	
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "code") String code) throws URISyntaxException, MalformedURLException
	{	
		/**The "No-auth" provider should have already saved a row with this code as the auth_id and type "No-auth".
		 * Now that the user is logging back in via email address, we should update the type to "Email-Account".
		 */
		authenticationProviderService.updateAuthProviderType(code, AuthenticationProviderType.EMAIL_ACCOUNT);

		httpServletRequest.getSession().setAttribute(CrsApplicationUser.SESSION_OBJECT_NAME, createCrsApplicationUser(code));
		
		// generate and store auth code
		String authCode = AuthCodeGenerator.generate();
		httpServletRequest.getSession().setAttribute("authCode", authCode);

		// get auth code url from properties
		String authCodeUrl = crsProperties.getProperty("authCodeUrl");

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(authCodeUrl + "/" + authCode)).build();
	}
	
	private CrsApplicationUser createCrsApplicationUser(String emailCode)
	{
		AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderId(emailCode);
		
		return new CrsApplicationUser(authProviderEntity.getCrsApplicationUserId(), emailCode, AuthenticationProviderType.EMAIL_ACCOUNT);
	}
}
