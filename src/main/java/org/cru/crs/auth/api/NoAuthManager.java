package org.cru.crs.auth.api;

import org.cru.crs.auth.model.BasicNoAuthUser;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.utils.AuthCodeGenerator;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Stateless
@Path("/auth/none")
public class NoAuthManager extends AbstractAuthManager
{
	@Path("/login")
	@GET
	public Response login() throws URISyntaxException, MalformedURLException
	{
		String noAuthId = AuthCodeGenerator.generate();

		BasicNoAuthUser basicNoAuthUser = BasicNoAuthUser.fromAuthIdAndEmail(noAuthId);

		persistIdentityAndAuthProviderRecordsIfNecessary(basicNoAuthUser);

		SessionEntity sessionEntity = persistSession(basicNoAuthUser);

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("clientUrl") 
										+ crsProperties.getProperty("authUrlPath") 
										+ "/"
										+ sessionEntity.getAuthCode())).build();
	}
}
