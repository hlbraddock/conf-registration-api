package org.cru.crs.auth.api;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.utils.AuthCodeGenerator;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Stateless
@Path("/auth/none")
public class NoAuthManager extends AbstractAuthManager
{
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "email") String email) throws URISyntaxException, MalformedURLException
	{
		String noAuthId = UUID.randomUUID().toString();

		// deny repeat usage of email no authentication login
		if(authenticationProviderService.findAuthProviderIdentityByAuthProviderUsernameAndType(email, AuthenticationProviderType.NONE) != null)
			return Response.status(Response.Status.UNAUTHORIZED).build();

		authenticationProviderService.createIdentityAndAuthProviderRecords(noAuthId, AuthenticationProviderType.NONE, email);

		CrsApplicationUser crsApplicationUser = createCrsApplicationUser(noAuthId, AuthenticationProviderType.NONE, email);

		httpServletRequest.getSession().setAttribute(CrsApplicationUser.SESSION_OBJECT_NAME, crsApplicationUser);

		String authCode = storeAuthCode(httpServletRequest, AuthCodeGenerator.generate());

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("authCodeUrl") + "/" + authCode)).build();
	}
}
