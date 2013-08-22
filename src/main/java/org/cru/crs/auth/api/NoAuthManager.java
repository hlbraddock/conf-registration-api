package org.cru.crs.auth.api;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.utils.AuthCodeGenerator;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
    public Response login(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
    {
        String noAuthId = UUID.randomUUID().toString();

        authenticationProviderService.createIdentityAndAuthProviderRecords(noAuthId, AuthenticationProviderType.NONE, null);

        httpServletRequest.getSession().setAttribute(CrsApplicationUser.SESSION_OBJECT_NAME, createCrsApplicationUser(noAuthId, AuthenticationProviderType.NONE, null));

        String authCode = storeAuthCode(httpServletRequest, AuthCodeGenerator.generate());

        // redirect to client managed auth code url with auth code
        return Response.seeOther(new URI(crsProperties.getProperty("authCodeUrl") + "/" + authCode)).build();
    }
}
