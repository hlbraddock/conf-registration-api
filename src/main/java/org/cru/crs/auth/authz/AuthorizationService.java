package org.cru.crs.auth.authz;

import java.util.UUID;

import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.jboss.resteasy.spi.UnauthorizedException;

public class AuthorizationService
{
	public void authorizeConference(ConferenceEntity conferenceEntity, OperationType operationType, CrsApplicationUser crsApplicationUser)
	{
		if(conferenceEntity == null) return;

		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}

		if(operationType.equals(OperationType.ADMIN) || operationType.equals(OperationType.DELETE) || operationType.equals(OperationType.UPDATE))
		{
			if(!isAdministeredBy(conferenceEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}
	}

	public void authorize(RegistrationEntity registrationEntity, ConferenceEntity conferenceEntity, OperationType operationType, CrsApplicationUser crsApplicationUser)
	{
		if(registrationEntity == null) return;

		if(crsApplicationUser == null)
		{
			throw new UnauthorizedException();
		}

		if(OperationType.CRUDSet.contains(operationType) && !operationType.equals(OperationType.DELETE))
		{
			if(!isOwnedBy(registrationEntity, crsApplicationUser.getId()) && !isAdministeredBy(conferenceEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}

		if(operationType.equals(OperationType.ADMIN) || operationType.equals(OperationType.DELETE))
		{
			if(!isAdministeredBy(conferenceEntity, crsApplicationUser.getId()))
			{
				throw new UnauthorizedException();
			}
		}
	}

	private boolean isOwnedBy(RegistrationEntity registrationEntity, UUID uuid)
	{
		return registrationEntity.getUserId().equals(uuid);
	}

	private boolean isAdministeredBy(ConferenceEntity conferenceEntity, UUID uuid)
	{
		return conferenceEntity.getContactPersonId().equals(uuid);
	}
}
