package org.cru.crs.auth;

import junit.framework.Assert;

import org.ccci.util.time.Clock;
import org.cru.crs.auth.api.TestAuthManager;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.auth.model.BasicNoAuthUser;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.service.SessionService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.CrsProperties;
import org.cru.crs.utils.CrsPropertiesFactory;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.joda.time.DateTime;
import org.sql2o.Sql2o;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User: lee.braddock
 */
public class CrsUserServiceTest
{
//	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
//	private EntityManagerFactory emFactory;
//	private EntityManager em;

	private CrsUserService crsUserService;
	private SessionService sessionService;
	private AuthenticationProviderService authenticationProviderService;
	private CrsProperties crsProperties;
	private Clock clock;

	@BeforeMethod
	public void setup()
	{

		sessionService = new SessionService(new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser"));
		crsProperties = new CrsPropertiesFactory().get();
		clock = new ClockImpl();

		crsUserService = new CrsUserService();
		crsUserService.sessionService = sessionService;
		crsUserService.authenticationProviderService = authenticationProviderService;
		crsUserService.crsProperties = crsProperties;
		crsUserService.clock = clock;
	}

	private String userEmail = "user@cru.org";

	@Test(groups = "db-integration-tests")
	public void testLogin()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, clock);

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate(), userEmail);


		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderUsername().equals(userEmail));

	}

	@Test(groups = "db-integration-tests", expectedExceptions = UnauthorizedException.class)
	public void testLoginThenExpiredSession()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, new ClockTestImpl(8));

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate(), userEmail);

		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderUsername().equals(userEmail));
	}

	@Test(groups = "db-integration-tests")
	public void testLoginThenGetLoggedInTwiceToCheckExtendedSession()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, new ClockTestImpl(3));

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate(), userEmail);

		String authCode = testAuthManager.login(authenticationProviderUser);

		CrsApplicationUser crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderUsername().equals(userEmail));

		crsApplicationUser = crsUserService.getLoggedInUser(authCode);

		Assert.assertTrue(crsApplicationUser.getAuthProviderUsername().equals(userEmail));
	}

	@Test(groups = "db-integration-tests", expectedExceptions = UnauthorizedException.class)
	public void testLoginExpiredSessionAfterFirstRequest()
	{
		TestAuthManager testAuthManager = TestAuthManager.getInstance(sessionService, authenticationProviderService, clock);

		AuthenticationProviderUser authenticationProviderUser = BasicNoAuthUser.fromAuthIdAndEmail(AuthCodeGenerator.generate(), userEmail);

		String authCode = testAuthManager.login(authenticationProviderUser);

		crsUserService.clock = new ClockTestImpl(4);

		CrsApplicationUser crsApplicationUser;
		try
		{
			crsApplicationUser = crsUserService.getLoggedInUser(authCode);
			Assert.assertTrue(crsApplicationUser.getAuthProviderUsername().equals(userEmail));
		}
		catch (UnauthorizedException e)
		{
			// we should not get this exception here
			Assert.assertTrue(false);
		}

		try
		{
			crsUserService.getLoggedInUser(authCode);
		}
		finally
		{
			// restore clock
			crsUserService.clock = clock;
		}
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
			return new DateTime().minusHours(hoursInPast);
		}
	}
}