package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;

@Stateless
@Path("/auth/relay")
public class RelayAuthManager
{

//	@Inject
	CrsProperties crsProperties = CrsProperties.get();
	
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
	{
		HttpSession session = httpServletRequest.getSession();
		
		CASReceipt casReceipt = (CASReceipt)session.getAttribute(CASFilter.CAS_FILTER_RECEIPT);

		if(casReceipt == null) Response.status(Status.SEE_OTHER).build();
		/*with attributes, fetch GUID and store in session*/
		session.setAttribute("relaySsoGuid", UUID.fromString((String)casReceipt.getAttributes().get("ssoGuid")));

		String authCode = AuthCodeGenerator.generate();
		httpServletRequest.getSession().setAttribute("authCode", authCode);

		// get auth code url from properties
		String authCodeUrl = crsProperties.getProperty("authCodeUrl");

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(authCodeUrl + "/" + authCode)).build();
	}

}
