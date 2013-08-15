package org.cru.crs.authz;

import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.model.RegistrationEntity;

import java.util.UUID;

public class AuthorizationService
{
	public void authorize(RegistrationEntity registrationEntity, OperationType operationType, CrsApplicationUser crsApplicationUser) throws UnauthorizedException
	{
		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}

		if(OperationType.CRUDSet.contains(operationType) && !operationType.equals(OperationType.DELETE))
		{
			if(!isOwnedBy(registrationEntity, crsApplicationUser.getId()) && !isAdministeredBy(registrationEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}

		if(operationType.equals(OperationType.ADMIN) || operationType.equals(OperationType.DELETE))
		{
			if(!isAdministeredBy(registrationEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}
	}

	private boolean isOwnedBy(RegistrationEntity registrationEntity, UUID uuid)
	{
		return registrationEntity.getUserId().equals(uuid);
	}

	private boolean isAdministeredBy(RegistrationEntity registrationEntity, UUID uuid)
	{
		return registrationEntity.getConference().getContactUser().equals(uuid);
	}
}
