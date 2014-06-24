package org.cru.crs.api.utils;

import com.google.common.base.Preconditions;
import org.cru.crs.api.model.Registration;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.auth.authz.OperationType;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;

import javax.inject.Inject;
import java.math.BigDecimal;

public class TotalDueBusinessLogic
{

	AuthorizationService authorizationService;
	ConferenceService conferenceService;
	ConferenceCostsService conferenceCostsService;
	
	@Inject
	public TotalDueBusinessLogic(AuthorizationService authorizationService, ConferenceService conferenceService, ConferenceCostsService conferenceCostsService)
	{
		this.authorizationService = authorizationService;
		this.conferenceService = conferenceService;
		this.conferenceCostsService = conferenceCostsService;
	}
	
	/**
	 * If the registration is being set to completed for the first time, then we need to calculate the total due
	 * based on the completed timestamp and early registration data.
	 */
	public void setTotalDue(Registration apiUpdatedRegistration,
								RegistrationEntity registrationEntityCopyOfApiRegistration,
								RegistrationEntity originalRegistrationEntity,
								CrsApplicationUser loggedInAdmin)
	{
		if(apiUpdatedRegistration.getCompleted()  && !originalRegistrationEntity.getCompleted())
		{
			ConferenceCostsEntity conferenceCostsEntity = conferenceCostsService.fetchBy(apiUpdatedRegistration.getConferenceId());
			
			registrationEntityCopyOfApiRegistration.setTotalDue(conferenceCostsEntity.getBaseCost());

			if(conferenceCostsEntity.isEarlyRegistrationDiscount() &&
					conferenceCostsEntity.getEarlyRegistrationCutoff() != null &&
					registrationEntityCopyOfApiRegistration.getCompletedTimestamp().isBefore(conferenceCostsEntity.getEarlyRegistrationCutoff()))
			{
				registrationEntityCopyOfApiRegistration.setTotalDue(conferenceCostsEntity.getBaseCost().subtract(conferenceCostsEntity.getEarlyRegistrationAmount()));
			}
		}
		
		administratorOverrideTotalDue(apiUpdatedRegistration, registrationEntityCopyOfApiRegistration, originalRegistrationEntity, loggedInAdmin);
		
		ensureStoredValuesDontGetErased(registrationEntityCopyOfApiRegistration, originalRegistrationEntity);
	}

	/**
	 * A user with update rights on the conference can set the totalDue to a different amount, but not for his/her own
	 * registration.
	 * 
	 * @param apiUpdatedRegistration - the values received from the client via web API
	 * @param registrationEntityCopyOfApiRegistration - a database entity copied from apiUpdatedRegistration. this object will eventually be saved to DB
	 * @param originalRegistrationEntity - a copy of the registrationEntity that is in the database at the time this update was requested
	 * @param loggedInAdmin - the user who is logged in.
	 */
	private void administratorOverrideTotalDue(Registration apiUpdatedRegistration,
													RegistrationEntity registrationEntityCopyOfApiRegistration,
													RegistrationEntity originalRegistrationEntity,
													CrsApplicationUser loggedInAdmin)
	{
		Preconditions.checkNotNull(originalRegistrationEntity);
		
		/* Get the original total due from either the originalRegistrationEntity (saved on a previous update)
		 * or in regstrationEntityCopyOfApiRegistration (calculated on this updated)
		 */
		BigDecimal originalTotalDue = originalRegistrationEntity.getTotalDue() != null ? originalRegistrationEntity.getTotalDue() 
																							: registrationEntityCopyOfApiRegistration.getTotalDue();
		
		/* If the updated registration's total due is null or zero, or if the updated registration
		 * and original registraiton's total due match.. then no update is needed
		 */
		if(apiUpdatedRegistration.getTotalDue() == null || 
				apiUpdatedRegistration.getTotalDue().equals(new BigDecimal("0")) ||
				apiUpdatedRegistration.getTotalDue().compareTo(originalTotalDue) == 0)
			return;

		authorizationService.authorizeConference(
				conferenceService.fetchConferenceBy(apiUpdatedRegistration.getConferenceId()), 
				OperationType.UPDATE,
				loggedInAdmin);

		registrationEntityCopyOfApiRegistration.setTotalDue(apiUpdatedRegistration.getTotalDue());
	}
	
	private void ensureStoredValuesDontGetErased(RegistrationEntity registrationEntityCopyOfApiRegistration, RegistrationEntity originalRegistrationEntity)
	{
		if(registrationEntityCopyOfApiRegistration.getCompletedTimestamp() == null && originalRegistrationEntity.getCompletedTimestamp() != null)
		{
			registrationEntityCopyOfApiRegistration.setCompletedTimestamp(originalRegistrationEntity.getCompletedTimestamp());
		}
		
		if(registrationEntityCopyOfApiRegistration.getTotalDue() == null && originalRegistrationEntity.getTotalDue() != null) 
		{
			registrationEntityCopyOfApiRegistration.setTotalDue(originalRegistrationEntity.getTotalDue());
		}
	}
}

