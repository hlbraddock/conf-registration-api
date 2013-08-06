package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;

@Stateless
@Path("/auth/email")
public class EmailAccountAuthManager
{
	CrsProperties crsProperties = CrsProperties.get();

	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "code") String code) throws URISyntaxException, MalformedURLException
	{
		httpServletRequest.getSession().setAttribute("emailAccount", UUID.randomUUID());
		
		// generate and store auth code
		String authCode = AuthCodeGenerator.generate();
		httpServletRequest.getSession().setAttribute("authCode", authCode);

		// get auth code url from properties
		String authCodeUrl = crsProperties.getProperty("authCodeUrl");

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(authCodeUrl + "/" + authCode)).build();
	}
}
