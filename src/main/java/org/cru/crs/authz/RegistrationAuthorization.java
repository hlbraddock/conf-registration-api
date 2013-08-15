package org.cru.crs.authz;

import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.model.RegistrationEntity;

import java.util.UUID;

public class RegistrationAuthorization
{
	public static void authorize(RegistrationEntity registrationEntity, CrsApplicationUser crsApplicationUser, OperationType operationType) throws UnauthorizedException
	{
		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}

		if(OperationType.CRUDSet.contains(operationType))
		{
			if(!isOwnedBy(registrationEntity, crsApplicationUser.getId()) && !isAdministeredBy(registrationEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}

		if(operationType.equals(OperationType.ADMIN))
		{
			if(!isAdministeredBy(registrationEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}
	}

	public static boolean isOwnedBy(RegistrationEntity registrationEntity, UUID uuid)
	{
		return registrationEntity.getUserId().equals(uuid);
	}

	public static boolean isAdministeredBy(RegistrationEntity registrationEntity, UUID uuid)
	{
		return registrationEntity.getConference().getContactUser().equals(uuid);
	}
}
