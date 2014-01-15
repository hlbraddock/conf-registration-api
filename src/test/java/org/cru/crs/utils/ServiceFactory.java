package org.cru.crs.utils;

import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.service.UserService;
import org.sql2o.Connection;

public class ServiceFactory {
	
	public static ConferenceService createConferenceService(Connection sqlConnection) {
		return new ConferenceService(sqlConnection,
				createConferenceCostsService(sqlConnection),
				createPageService(sqlConnection), 
				new UserService(sqlConnection),
				createPermissionService(sqlConnection));
	}
	
	public static PageService createPageService(Connection sqlConnection) {
		return new PageService(sqlConnection, createBlockService(sqlConnection));
	}

	public static BlockService createBlockService(Connection sqlConnection) {
		 return new BlockService(sqlConnection, createAnswerService(sqlConnection));
	}

	public static ConferenceCostsService createConferenceCostsService(Connection sqlConnection) {
		return new ConferenceCostsService(sqlConnection);
	}

	public static PermissionService createPermissionService(Connection sqlConnection) {
		return new PermissionService(sqlConnection);
	}

	public static RegistrationService createRegistrationService(Connection sqlConnection) {
		return new RegistrationService(sqlConnection, createAnswerService(sqlConnection), new PaymentService(sqlConnection));
	}
	
	private static AnswerService createAnswerService(Connection sqlConnection) {
		return new AnswerService(sqlConnection);
	}

}
