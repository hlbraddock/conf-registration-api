package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.cru.crs.auth.model.BasicNoAuthUser;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.utils.AuthCodeGenerator;

@Stateless
@Path("/auth/none")
public class NoAuthManager extends AbstractAuthManager
{
    @Path("/login")
    @GET
    public Response login(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
    {
        BasicNoAuthUser noAuthUser = BasicNoAuthUser.fromCode(UUID.randomUUID().toString());
		
        authenticationProviderService.createIdentityAndAuthProviderRecords(noAuthUser);

        httpServletRequest.getSession().setAttribute(CrsApplicationUser.SESSION_OBJECT_NAME, createCrsApplicationUser(noAuthUser));

        String authCode = storeAuthCode(httpServletRequest, AuthCodeGenerator.generate());

        // redirect to client managed auth code url with auth code
        return Response.seeOther(new URI(crsProperties.getProperty("authCodeUrl") + "/" + authCode)).build();
    }
}
