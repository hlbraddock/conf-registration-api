package org.cru.crs.auth.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.FacebookUser;
import org.cru.crs.auth.OauthServices;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.service.IdentityService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.JsonUtils;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.common.base.Strings;

/**
 * User: Lee_Braddock
 */
@Stateless
@Path("/auth/facebook")
public class FacebookAuthManager
{
	@Inject
	CrsProperties crsProperties;

	@Inject IdentityService authenticationProviderService;
	
	@Path("/authorization")
	@GET
	public Response authorization(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
	{
		String apiKey = crsProperties.getProperty("facebookAppId");
		String apiSecret = crsProperties.getProperty("facebookAppSecret");

		// get oauth service
		OAuthService service = OauthServices.build(FacebookApi.class, getUrlWithService(httpServletRequest, "login").toString(), apiKey, apiSecret);

		// get authorization url for identity provider
		String authorizationUrl = service.getAuthorizationUrl(null);

		// redirect to authorization url
		return Response.seeOther(new URI(authorizationUrl)).build();
	}

	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "code") String code, @QueryParam(value = "error") String error) throws URISyntaxException, MalformedURLException
	{
		if (isLoginError(code, error))
			return Response.status(Response.Status.UNAUTHORIZED).build();

		// get verifier for identity provider provided code
		Verifier verifier = new Verifier(code);

		String apiKey = crsProperties.getProperty("facebookAppId");
		String apiSecret = crsProperties.getProperty("facebookAppSecret");

		// get oauth service
		OAuthService service = OauthServices.build(FacebookApi.class, getUrlWithService(httpServletRequest, "login").toString(), apiKey, apiSecret);

		// get identity provider access token
		Token accessToken;
		try
		{
			accessToken = service.getAccessToken(null, verifier);
		}
		catch(Exception	e)
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		// build request
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");

		// sign in to facebook on behalf of user using access token
		service.signRequest(accessToken, request);

		// send request for user info
		org.scribe.model.Response response = request.send();

		// check response code
		if (response.getCode() != 200)
			return Response.status(response.getCode()).build();

		// transform the facebook response into facebook user
		FacebookUser facebookUser = FacebookUser.fromJsonNode(JsonUtils.jsonNodeFromString(response.getBody()));
		
		//Create a CRS user object and stick it in the session
		httpServletRequest.getSession().setAttribute(CrsApplicationUser.SESSION_OBJECT_NAME, createCrsApplicationUser(facebookUser.getId()));
		
		// generate and store auth code
		String authCode = AuthCodeGenerator.generate();
		httpServletRequest.getSession().setAttribute("authCode", authCode);

		// get auth code url from properties
		String authCodeUrl = crsProperties.getProperty("authCodeUrl");

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(authCodeUrl + "/" + authCode)).build();
	}

	private boolean isLoginError(String code, String error)
	{
		if (Strings.isNullOrEmpty(code))
			return true;

		return !Strings.isNullOrEmpty(error);
	}

	private URL getUrlWithService(HttpServletRequest servletRequest, String serviceName) throws MalformedURLException
	{
		String requestUrl = servletRequest.getRequestURL().toString();

		return new URL(requestUrl.substring(0, requestUrl.lastIndexOf("/")) + "/" + serviceName);
	}
	
	private CrsApplicationUser createCrsApplicationUser(String facebookId)
	{
		AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderId(facebookId);
		
		return new CrsApplicationUser(authProviderEntity.getCrsApplicationUserId(), facebookId, AuthenticationProviderType.FACEBOOK);
	}
}
