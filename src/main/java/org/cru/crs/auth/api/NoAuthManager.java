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
import org.cru.crs.auth.model.BasicNoAuthUser;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.MailService;

@Stateless
@Path("/auth/none")
public class NoAuthManager extends AbstractAuthManager
{
	@Inject
	MailService mailService;

	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "email") String email) throws URISyntaxException, MalformedURLException
	{
		String noAuthId = AuthCodeGenerator.generate();

		// deny repeat usage of email no authentication login
		if(!crsProperties.getProperty("mode").equals("debug"))
		{
			if(authenticationProviderService.findAuthProviderIdentityByAuthProviderUsernameAndType(email, AuthenticationProviderType.NONE) != null)
			{
				// TODO need a more user friendly response
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		}

		authenticationProviderService.createIdentityAndAuthProviderRecords(BasicNoAuthUser.fromAuthIdAndEmail(noAuthId, email));

		sendLoginLink(httpServletRequest, email, noAuthId);

		BasicNoAuthUser basicNoAuthUser = BasicNoAuthUser.fromAuthIdAndEmail(noAuthId, email);

		String authCode = AuthCodeGenerator.generate();

		persistSession(basicNoAuthUser, authCode);

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("clientUrl") + "auth/" + authCode)).build();
	}

	/*
	 * Send welcome to user with return login link
	 */
	private void sendLoginLink(HttpServletRequest httpServletRequest, String email, String noAuthId)
	{
		try
		{
			String loginLink = httpServletRequest.getRequestURL().toString().replace("auth/none/login", "auth/email/login?authId=" + noAuthId);

			mailService.send(crsProperties.getProperty("crsEmail"), email, "Cru CRS Login", getMessage(loginLink));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getMessage(String loginLink)
	{
		String message = "";

		message += "<p>" + "Hello!" + "</p>";
		message += "<p>" + "Welcome to the Cru Conference Registration System ( " + crsProperties.getProperty("clientUrl") + " )</p>";
		message += "<p>" + "You recently logged with this email as your username." + "</p>";
		message += "<p>" + "If you'd like to log in again, simply click <a href=" + loginLink + ">Cru CRS</a> ";
		message += "(or you can go here " + loginLink + " )</p>";
		message += "<p>" + "Happy Conferencing!" + "</p>";
		message += "<p>" + "Cru CRS Team" + "</p>";

		return message;
	}
}
