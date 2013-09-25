package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.EmailAccountUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.utils.AuthCodeGenerator;

@Stateless
@Path("/auth/email")
public class EmailAccountAuthManager extends AbstractAuthManager
{
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "authId") String authId) throws URISyntaxException, MalformedURLException
	{	
		/**The "No-auth" provider should have already saved a row with this code as the auth_id and type "No-auth".
		 * Now that the user is logging back in via email address, we should update the type to "Email-Account".
		 */
		AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity = authenticationProviderService.updateAuthProviderType(authId, AuthenticationProviderType.EMAIL_ACCOUNT);

		if(authenticationProviderIdentityEntity == null)
		{
			// TODO need a more user friendly response
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		EmailAccountUser emailAccountUser = EmailAccountUser.fromAuthIdAndEmail(authId, authenticationProviderIdentityEntity.getUsername());

		String authCode = AuthCodeGenerator.generate();

		persistSession(emailAccountUser, authCode);

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("clientUrl") + "auth/" + authCode)).build();
	}
}
