package org.cru.crs.auth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.cru.crs.auth.OauthServices;
import org.cru.crs.auth.model.FacebookUser;
import org.cru.crs.model.SessionEntity;
import org.cru.crs.utils.JsonNodeHelper;
import org.cru.crs.utils.Simply;
import org.cru.crs.utils.URL;
import org.jboss.logging.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: Lee_Braddock
 */
@Stateless
@Path("/auth/facebook")
public class FacebookAuthManager extends AbstractAuthManager
{
	Logger logger = Logger.getLogger(getClass());

	@Path("/authorization")
	@GET
	public Response authorization(@Context HttpServletRequest httpServletRequest) throws URISyntaxException, MalformedURLException
	{
		String apiKey = crsProperties.getProperty("facebookAppId");
		String apiSecret = crsProperties.getProperty("facebookAppSecret");
		String apiServerProtocol = crsProperties.getNonNullProperty("apiServerProtocol");
		String apiServerPort = crsProperties.getNonNullProperty("apiServerPort");
		String apiServerName = crsProperties.getNonNullProperty("apiServerName");

		// get oauth service
		OAuthService service = new ServiceBuilder()
				.provider(FacebookApi.class)
				.apiKey(apiKey)
				.apiSecret(apiSecret)
				.callback(getServiceUrl(httpServletRequest.getRequestURL().toString(), apiServerProtocol, apiServerPort, apiServerName, "login"))
				.scope("email")
				.build();

		// get authorization url for identity provider
		String authorizationUrl = service.getAuthorizationUrl(null);

		// redirect to authorization url
		return Response.seeOther(new URI(authorizationUrl)).build();
	}

	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest httpServletRequest, @QueryParam(value = "code") String code, @QueryParam(value = "error") String error) throws URISyntaxException, MalformedURLException
	{
		logger.info("facebook::login()");
		if (isLoginError(code, error))
			return Response.status(Response.Status.UNAUTHORIZED).build();

		// get verifier for identity provider provided code
		Verifier verifier = new Verifier(code);

		logger.info("facebook::login() - got verifier");

		String apiKey = crsProperties.getProperty("facebookAppId");
		String apiSecret = crsProperties.getProperty("facebookAppSecret");
		String apiServerProtocol = crsProperties.getNonNullProperty("apiServerProtocol");
		String apiServerPort = crsProperties.getNonNullProperty("apiServerPort");
		String apiServerName = crsProperties.getNonNullProperty("apiServerName");

		// get oauth service
		OAuthService service =
				OauthServices.build(FacebookApi.class, getServiceUrl(httpServletRequest.getRequestURL().toString(), apiServerProtocol, apiServerPort, apiServerName, "login"), apiKey, apiSecret);

		logger.info("facebook::login() - got service");

		// get identity provider access token
		Token accessToken;
		try
		{
			accessToken = service.getAccessToken(null, verifier);
			logger.info("facebook::login() - got token");
		}
		catch(Exception	e)
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		// build request
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");

		logger.info("facebook::login() - got request");

		// sign in to facebook on behalf of user using access token
		service.signRequest(accessToken, request);

		logger.info("facebook::login() - got sign request");

		// send request for user info
		org.scribe.model.Response response = request.send();

		logger.info("facebook::login() - got response " + response.getCode());

		// check response code
		if (response.getCode() != 200)
			return Response.status(response.getCode()).build();

		// transform the facebook response into facebook user
		FacebookUser facebookUser;
		try
		{
			String body = response.getBody();

			logger.info("facebook::login() - got body " + body);

			JsonNode jsonNodeFacebookUser = JsonNodeHelper.toJsonNode(body);

			logger.info("facebook::login() - got json" );

			Simply.logObject(jsonNodeFacebookUser, getClass());

			facebookUser = new FacebookUser(jsonNodeFacebookUser, accessToken.getToken());

			logger.info("facebook::login() - got facebook user");

			Simply.logObject(facebookUser, getClass());
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

        persistIdentityAndAuthProviderRecordsIfNecessary(facebookUser);

		logger.info("facebook::login() - persisted user");

		SessionEntity sessionEntity = persistSession(facebookUser);

		logger.info("facebook::login() - persisted session");

		// redirect to client managed auth code url with auth code
		return Response.seeOther(new URI(crsProperties.getProperty("clientUrl") 
									+ crsProperties.getProperty("authUrlPath") 
									+ "/"
									+ sessionEntity.getAuthCode())).build();
	}

	private boolean isLoginError(String code, String error)
	{
		if (Strings.isNullOrEmpty(code))
			return true;

		return !Strings.isNullOrEmpty(error);
	}

	private String getServiceUrl(String serviceUrl, String protocol, String port, String host, String serviceName) throws MalformedURLException
	{
		URL url = new URL(serviceUrl).withProtocol(protocol).withPort(port).withHost(host).simplify();

		String newServiceUrl = StringUtils.substringBeforeLast(url.toExternalForm(), "/") + "/" + serviceName;

		logger.info("serviceUrl:" + serviceUrl + ": to:" + newServiceUrl + ":");

		return newServiceUrl;
	}
}
