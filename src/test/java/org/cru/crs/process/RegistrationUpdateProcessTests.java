package org.cru.crs.process;

import java.math.BigDecimal;
import java.util.UUID;

import junit.framework.Assert;

import org.ccci.util.time.Clock;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.process.RegistrationFetchProcess;
import org.cru.crs.api.process.RegistrationUpdateProcess;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationUpdateProcessTests
{
	org.sql2o.Connection sqlConnection;
	
	RegistrationUpdateProcess process;
	RegistrationService registrationService;
	AnswerService answerService;
	PaymentService paymentService;
	
	RegistrationFetchProcess registrationFetchProcess;
	
	Registration testRegistration;
	
	Clock clock;
	
	final UUID conferenceId = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");
	
	@BeforeMethod
	public void setup()
	{
		sqlConnection = new SqlConnectionProducer().getTestSql2oConnection();
		
		answerService = new AnswerService(sqlConnection);
		paymentService = new PaymentService(sqlConnection);
		registrationService = new RegistrationService(sqlConnection, answerService, paymentService);
		ConferenceCostsService conferenceCostsService = new ConferenceCostsService(sqlConnection);
		BlockService blockService = new BlockService(sqlConnection, answerService);
		ConferenceService conferenceService = new ConferenceService(sqlConnection,
													new ConferenceCostsService(sqlConnection),
													new PageService(sqlConnection, new BlockService(sqlConnection, new AnswerService(sqlConnection))), 
													new UserService(sqlConnection));
		
		registrationFetchProcess = new RegistrationFetchProcess(registrationService, paymentService, answerService);
		
		clock = new Clock(){

			@Override
			public DateTime currentDateTime()
			{
				return DateTimeCreaterHelper.createDateTime(2013, 9, 30, 16, 0, 0);
			}};
			
		process = new RegistrationUpdateProcess(registrationService,answerService,conferenceService, conferenceCostsService, clock);
	}
	
	@BeforeMethod
	public void createTestRegistration()
	{
		testRegistration = new Registration();
		testRegistration.setId(UUID.randomUUID());
		testRegistration.setConferenceId(conferenceId);
	}
	
	@Test
	public void testSetCompleted()	
	{
		testRegistration.setCompleted(true);
		
		try
		{
			registrationService.createNewRegistration(testRegistration.toDbRegistrationEntity());
			
			process.performDeepUpdate(testRegistration);
			
			Registration updatedRegistration = registrationFetchProcess.get(testRegistration.getId());
			
			Assert.assertTrue(updatedRegistration.getCompleted());
			Assert.assertEquals(new BigDecimal(50.00d).doubleValue(), updatedRegistration.getTotalDue().doubleValue());
			Assert.assertEquals(clock.currentDateTime(), updatedRegistration.getCompletedTimestamp());
		}
		finally
		{

		}
	}

}
