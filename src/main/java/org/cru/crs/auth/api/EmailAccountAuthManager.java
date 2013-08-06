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
import javax.ws.rs.core.Response.Status;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.service.IdentityService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;

@Stateless
@Path("/auth/email")
public class EmailAccountAuthManager
{
	CrsProperties crsProperties = CrsProperties.get();

	@Inject IdentityService authenticationProviderService;
	
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "code") String code) throws URISyntaxException, MalformedURLException
	{
		code = "xxxyyy";
		
		AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByEmailCode(code);
		
		if(authProviderEntity == null)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		// set email account auth provider ID into session
		httpServletRequest.getSession().setAttribute(AuthenticationProviderType.EMAIL_ACCOUNT.getSessionIdentifierName(), 
														authProviderEntity.getAuthenticationProviderId());
		
		// generate and store auth code
		String authCode = AuthCodeGenerator.generate();
		httpServletRequest.getSession().setAttribute("authCode", authCode);

		// get auth code url from properties
		String authCodeUrl = crsProperties.getProperty("authCodeUrl");

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(authCodeUrl + "/" + authCode)).build();
	}
}
