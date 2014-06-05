package org.cru.crs.process;

import java.math.BigDecimal;
import java.util.UUID;

import org.ccci.util.time.Clock;
import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.ProfileProcess;
import org.cru.crs.api.process.RetrieveRegistrationProcess;
import org.cru.crs.api.process.UpdateRegistrationProcess;
import org.cru.crs.api.utils.TotalDueBusinessLogic;
import org.cru.crs.jaxrs.UnauthorizedException;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UpdateRegistrationProcessTest extends AbstractTestWithDatabaseConnectivity
{
	UpdateRegistrationProcess process;

	RegistrationService registrationService;

	RetrieveRegistrationProcess registrationFetchProcess;

	Registration testRegistration;

	Clock clock;
	
	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		refreshConnection();

		PaymentService paymentService = ServiceFactory.createPaymentService(sqlConnection);
		BlockService blockService = ServiceFactory.createBlockService(sqlConnection);
		PageService pageService = ServiceFactory.createPageService(sqlConnection);
		ProfileService profileService = ServiceFactory.createProfileService(sqlConnection);
		
		ProfileProcess profileProcess = new ProfileProcess(blockService, profileService, pageService, new UserService(sqlConnection));
		
		TotalDueBusinessLogic totalDueBusinessLogic = new TotalDueBusinessLogic(ServiceFactory.createAuthorizationService(sqlConnection), 
																ServiceFactory.createConferenceService(sqlConnection), 
																ServiceFactory.createConferenceCostsService(sqlConnection));
				
		registrationService = ServiceFactory.createRegistrationService(sqlConnection);

		registrationFetchProcess = new RetrieveRegistrationProcess(registrationService, paymentService, ServiceFactory.createAnswerService(sqlConnection));

		clock = new Clock()
		{
			@Override
			public DateTime currentDateTime()
			{
				return DateTimeCreaterHelper.createDateTime(2013, 9, 30, 16, 0, 0);
			}
		};

		process = new UpdateRegistrationProcess(
						registrationService,
						ServiceFactory.createAnswerService(sqlConnection),
						ServiceFactory.createConferenceService(sqlConnection),
						profileProcess,
						totalDueBusinessLogic,
						clock
				);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void createTestRegistration()
	{
		testRegistration = new Registration();
		testRegistration.setId(UUID.randomUUID());
		testRegistration.setConferenceId(ConferenceInfo.Id.FallBeach);
		testRegistration.setUserId(UserInfo.Id.TestUser);
	}
	
	@Test(groups="dbtest")
	public void testSetCompleted()	
	{
		try
		{
			registrationService.createNewRegistration(testRegistration.toDbRegistrationEntity());

			testRegistration.setCompleted(true);

			process.performDeepUpdate(testRegistration, UserInfo.Users.TestUser);
			
			Registration updatedRegistration = registrationFetchProcess.get(testRegistration.getId());
			
			Assert.assertTrue(updatedRegistration.getCompleted());
			Assert.assertEquals(updatedRegistration.getTotalDue(), new BigDecimal("125.00"));
			Assert.assertEquals(updatedRegistration.getCompletedTimestamp(), clock.currentDateTime());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest")
	public void testAdministratorOverrideTotalDue()
	{
		try
		{
			registrationService.createNewRegistration(testRegistration.toDbRegistrationEntity());

			testRegistration.setCompleted(true);
			testRegistration.setTotalDue(new BigDecimal("100.00"));

			/*Ryan is the creator of this conference, so he should be able to change it*/
			process.performDeepUpdate(testRegistration, UserInfo.Users.Ryan);
			
			Assert.assertEquals(registrationService.getRegistrationBy(testRegistration.getId()).getTotalDue(), new BigDecimal("100.00"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test(groups="dbtest", expectedExceptions=UnauthorizedException.class)
	public void testAdministratorOverrideTotalDueUnauthorized()
	{
		try
		{
			registrationService.createNewRegistration(testRegistration.toDbRegistrationEntity());

			testRegistration.setCompleted(true);

			/*set the registration to be complete.  this sets the total due to $125.00*/
			process.performDeepUpdate(testRegistration, UserInfo.Users.TestUser);

			Assert.assertEquals(registrationService.getRegistrationBy(testRegistration.getId()).getTotalDue(), new BigDecimal("125.00"));

			testRegistration.setTotalDue(new BigDecimal("100.00"));

			/*TestUser does not have admin rights for this conference, so he should
			 * not be able alter the total due. */
			process.performDeepUpdate(testRegistration, UserInfo.Users.TestUser);
		}
		finally 
		{
			sqlConnection.rollback();
		}
	}
	
	
	@Test(groups="dbtest")
	public void testAdministratorOverrideTotalDueToNull()
	{
		try
		{
			registrationService.createNewRegistration(testRegistration.toDbRegistrationEntity());

			testRegistration.setCompleted(true);

			/*set the registration to be complete.  this sets the total due to $125.00*/
			process.performDeepUpdate(testRegistration, UserInfo.Users.TestUser);

			Assert.assertEquals(registrationService.getRegistrationBy(testRegistration.getId()).getTotalDue(), new BigDecimal("125.00"));
			
			testRegistration.setTotalDue(null);

			/*Ryan is the creator of this conference, so he should be able to change it*/
			process.performDeepUpdate(testRegistration, UserInfo.Users.Ryan);
			
			Assert.assertEquals(registrationService.getRegistrationBy(testRegistration.getId()).getTotalDue(), new BigDecimal("125.00"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
