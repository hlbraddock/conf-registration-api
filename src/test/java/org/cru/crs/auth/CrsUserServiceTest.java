package org.cru.crs.auth;

import org.ccci.util.time.Clock;
import org.cru.crs.AbstractServiceTest;
import org.cru.crs.auth.api.TestAuthManager;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.auth.model.BasicNoAuthUser;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.auth.model.RelayUser;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.jaxrs.UnauthorizedException;
import org.cru.crs.model.ProfileEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.SessionService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;


/**
 * User: lee.braddock
 */
public class CrsUserServiceTest extends AbstractServiceTest
{
	private CrsUserService crsUserService;
	private SessionService sessionService;
	private AuthenticationProviderService authenticationProviderService;
	private ProfileService profileService;
	private UserService userService;
	private CrsProperties crsProperties;
	private Clock clock;

	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		sessionService = new SessionService(sqlConnection);
		crsProperties = new CrsPropertiesFactory().get();
		authenticationProviderService = new AuthenticationProviderService(sqlConnection);
		userService = new UserService(sqlConnection);
		profileService = new ProfileService(sqlConnection);
		clock = new ClockImpl();

		crsUserService = new CrsUserService();
		crsUserService.sessionService = sessionService;
		crsUserService.authenticationProviderService = authenticationProviderService;
		crsUserService.crsProperties = crsProperties;
		crsUserService.clock = clock;
	}

	@Test(groups = "dbtest")
	public void testLogin()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, clock, profileService, userService);

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate());

		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderType().equals(AuthenticationProviderType.NONE));
	}

	@Test(groups = "dbtest")
	public void testLoginWithCapturedProfile()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, clock, profileService, userService);

		String username = "clive.staples@oxford.edu";
		String email = "clive.staples@oxford.edu";
		String first = "Clive";
		String last = "Lewis";

		AuthenticationProviderUser authenticationProviderUser = RelayUser.fromAuthId(AuthCodeGenerator.generate(), username, email, first, last);

		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderType().equals(AuthenticationProviderType.RELAY));

		ProfileEntity profileEntity = profileService.getProfileByUser(crsApplicationUser.getId());

		Assert.assertNotNull(profileEntity);

		Assert.assertEquals(username, profileEntity.getEmail());
		Assert.assertEquals(first, profileEntity.getFirstName());
		Assert.assertEquals(last, profileEntity.getLastName());
	}

	@Test(groups = "dbtest", expectedExceptions = UnauthorizedException.class)
	public void testLoginThenExpiredSession()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, new ClockTestImpl(8), profileService, userService);

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate());

		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderType().equals(AuthenticationProviderType.NONE));
	}

	@Test(groups = "dbtest")
	public void testLoginThenGetLoggedInTwiceToCheckExtendedSession()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, new ClockTestImpl(3), profileService, userService);

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate());

		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderType().equals(AuthenticationProviderType.NONE));
	}

	@Test(groups = "dbtest", expectedExceptions = UnauthorizedException.class)
	public void testLoginExpiredSessionAfterFirstRequest()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, clock, profileService, userService);

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate());

		String authCode = testAuthManager.login(authenticationProviderUser);

		crsUserService.clock = new ClockTestImpl(4);

		CrsApplicationUser crsApplicationUser;
		try
		{
			crsApplicationUser = crsUserService.getLoggedInUser(authCode);
			Assert.assertTrue(crsApplicationUser.getAuthProviderType().equals(AuthenticationProviderType.NONE));
		}
		catch (WebApplicationException e)
		{
			// we should not get this exception here
			Assert.assertTrue(false);
		}

		crsUserService.getLoggedInUser(authCode);
	}

	private class ClockTestImpl extends Clock
	{
		private int hoursInPast = 0;

		private ClockTestImpl(int hoursInPast)
		{
			this.hoursInPast = hoursInPast;
		}

		@Override
		public DateTime currentDateTime()
		{
			return new DateTime(DateTimeZone.UTC).minusHours(hoursInPast);
		}
	}
}
