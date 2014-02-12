package org.cru.crs.utils;

import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.RegistrationViewService;
import org.cru.crs.service.UserService;
import org.sql2o.Connection;

public class ServiceFactory
{
	
	public static ConferenceService createConferenceService(Connection sqlConnection)
	{
		return new ConferenceService(sqlConnection,
				createConferenceCostsService(sqlConnection),
				createPageService(sqlConnection), 
				new UserService(sqlConnection),
				createPermissionService(sqlConnection));
	}
	
	public static PageService createPageService(Connection sqlConnection)
	{
		return new PageService(sqlConnection, createBlockService(sqlConnection));
	}

	public static BlockService createBlockService(Connection sqlConnection)
	{
		 return new BlockService(sqlConnection, createAnswerService(sqlConnection));
	}

	public static ConferenceCostsService createConferenceCostsService(Connection sqlConnection)
	{
		return new ConferenceCostsService(sqlConnection);
	}

	public static PermissionService createPermissionService(Connection sqlConnection)
	{
		return new PermissionService(sqlConnection);
	}

	public static UserService createUserService(Connection sqlConnection)
	{
		return new UserService(sqlConnection);
	}
	
	public static RegistrationService createRegistrationService(Connection sqlConnection)
	{
		return new RegistrationService(sqlConnection, createAnswerService(sqlConnection), new PaymentService(sqlConnection));
	}
	
	public static AnswerService createAnswerService(Connection sqlConnection)
	{
		return new AnswerService(sqlConnection);
	}
	
	public static RegistrationViewService createRegistrationViewService(Connection sqlConnection)
	{
		return new RegistrationViewService(sqlConnection, createPageService(sqlConnection), createBlockService(sqlConnection));
	}

	public static PaymentService createPaymentService(Connection sqlConnection)
	{
		return new PaymentService(sqlConnection);
	}

	public static ProfileService createProfileService(Connection sqlConnection)
	{
		return new ProfileService(sqlConnection);
	}
	
	public static AuthorizationService createAuthorizationService(Connection sqlConnection)
	{
		return new AuthorizationService(createPermissionService(sqlConnection), createRegistrationService(sqlConnection));
	}

}
