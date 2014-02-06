package org.cru.crs.auth.api;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.cru.crs.auth.model.RelayUser;
import org.cru.crs.model.SessionEntity;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Stateless
@Path("/auth/relay")
public class RelayAuthManager extends AbstractAuthManager
{
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
	{
		HttpSession session = httpServletRequest.getSession();
		
		RelayUser relayUser = RelayUser.fromCasReceipt((CASReceipt)session.getAttribute(CASFilter.CAS_FILTER_RECEIPT));
		
		persistIdentityAndAuthProviderRecordsIfNecessary(relayUser);

		SessionEntity sessionEntity = persistSession(relayUser);

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("clientUrl") 
											+ crsProperties.getProperty("authUrlPath") 
											+ "/"
											+ sessionEntity.getAuthCode())).build();
	}
}
