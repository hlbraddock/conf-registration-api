package org.cru.crs.process;

import java.math.BigDecimal;
import java.util.UUID;

import junit.framework.Assert;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.ProfileProcess;
import org.cru.crs.api.process.RetrieveRegistrationProcess;
import org.cru.crs.api.process.UpdateRegistrationProcess;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UpdateRegistrationProcessTest
{
	org.sql2o.Connection sqlConnection;

	UpdateRegistrationProcess process;

	RegistrationService registrationService;

	RetrieveRegistrationProcess registrationFetchProcess;
	
	Registration testRegistration;

	Clock clock;

	final UUID conferenceId = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");
	
	@BeforeMethod(alwaysRun=true)
	public void setup()
	{
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		AnswerService answerService = new AnswerService(sqlConnection);
		PaymentService paymentService = new PaymentService(sqlConnection);
		BlockService blockService = new BlockService(sqlConnection, answerService);
		UserService userService = new UserService(sqlConnection);
		PageService pageService = new PageService(sqlConnection, blockService);
		ConferenceCostsService conferenceCostsService = new ConferenceCostsService(sqlConnection);
		PermissionService permissionService = new PermissionService(sqlConnection);
		ConferenceService conferenceService = new ConferenceService(sqlConnection, conferenceCostsService, pageService, userService, permissionService);
		ProfileService profileService = new ProfileService(sqlConnection);
		ProfileProcess profileProcess = new ProfileProcess(blockService, profileService, pageService);

		registrationService = new RegistrationService(sqlConnection, answerService, paymentService);

		registrationFetchProcess = new RetrieveRegistrationProcess(registrationService, paymentService, answerService);

		clock = new Clock()
		{
			@Override
			public DateTime currentDateTime()
			{
				return DateTimeCreaterHelper.createDateTime(2013, 9, 30, 16, 0, 0);
			}
		};

		process = new UpdateRegistrationProcess(registrationService,answerService,conferenceService, conferenceCostsService, clock, profileProcess);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void createTestRegistration()
	{
		testRegistration = new Registration();
		testRegistration.setId(UUID.randomUUID());
		testRegistration.setConferenceId(conferenceId);
	}
	
	@Test(groups="dbtest")
	public void testSetCompleted()	
	{
		try
		{
			registrationService.createNewRegistration(testRegistration.toDbRegistrationEntity());

			testRegistration.setCompleted(true);

			process.performDeepUpdate(testRegistration);
			
			Registration updatedRegistration = registrationFetchProcess.get(testRegistration.getId());
			
			Assert.assertTrue(updatedRegistration.getCompleted());
			Assert.assertEquals(new BigDecimal("50.00"), updatedRegistration.getTotalDue());
			Assert.assertEquals(clock.currentDateTime(), updatedRegistration.getCompletedTimestamp());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

}
