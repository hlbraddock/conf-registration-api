package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;

@Stateless
@Path("/auth/relay")
public class RelayAuthManager extends AbstractAuthManager
{
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
	{
		HttpSession session = httpServletRequest.getSession();
		
		CASReceipt casReceipt = (CASReceipt)session.getAttribute(CASFilter.CAS_FILTER_RECEIPT);

		/*we can assume this is not null, the CAS filter would redirect to the CAS server for login otherwise*/
		String ssoGuidString = casReceipt.getAttributes().get("ssoGuid").toString().toLowerCase();
		
		persistIdentityAndAuthProviderRecordsIfNecessary(ssoGuidString, AuthenticationProviderType.RELAY);
		
		/*with attributes, fetch GUID and store in session*/
		session.setAttribute(CrsApplicationUser.SESSION_OBJECT_NAME, createCrsApplicationUser(ssoGuidString, AuthenticationProviderType.RELAY));

        String authCode = generateAndStoreAuthCodeInSession(httpServletRequest);

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("authCodeUrl") + "/" + authCode)).build();
	}
}
