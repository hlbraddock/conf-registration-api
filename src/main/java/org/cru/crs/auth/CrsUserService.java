package org.cru.crs.auth;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.ExternalIdentityEntity;
import org.cru.crs.service.IdentityService;

import com.google.common.base.Preconditions;

public class CrsUserService
{
	IdentityService externalIdentityService;
	
	@Inject
	public CrsUserService(IdentityService externalIdentityService)
	{
		this.externalIdentityService = externalIdentityService;
	}
	
	public CrsApplicationUser buildCrsApplicationUserFromDataIn(HttpSession httpSession)
	{
		/*look in the session to see if there's an external identity id we know about*/
		ExternalIdentityAuthenticationProviderAndId externalIdentityInfo = checkSessionForExternalIdFromKnownIdentityProviders(httpSession);
		
		/*if not, then there's nothing further we can do*/
		if(externalIdentityInfo == null) return null;
		
		/*we have an id, now go look for the row in our database to hopefully find an internal user*/
		ExternalIdentityEntity externalIdentityEntity = externalIdentityService.findExternalIdentityBy(externalIdentityInfo.getAuthProviderId());
		
		if(externalIdentityEntity == null)
		{
			/*oops, this person has not previously logged in.  let's create a row for them so next time we'll
			 * know who they are*/
			externalIdentityService.createIdentityRecords(externalIdentityInfo);
			/*and fetch the information out. it wasn't elegant enough to just return it*/
			externalIdentityEntity = externalIdentityService.findExternalIdentityBy(externalIdentityInfo.getAuthProviderId());
		}
		
		/*finally return the CRS application id caller asked for*/
		return new CrsApplicationUser(externalIdentityEntity.getCrsApplicationUserId(), externalIdentityInfo);
	}

	/**
	 * Returns true if there is an appUserId and it matches the user Id on the conference
	 * 
	 * preconditions: conference must not be null
	 * 
	 * @param conference
	 * @param appUserId
	 * @return
	 */
	public boolean isUserAuthorizedOnConference(ConferenceEntity conference, UUID appUserId)
	{
		Preconditions.checkNotNull(conference);
		
		return appUserId != null && appUserId.equals(conference.getContactUser());
	}
	
	/**
	 * Right now the known providers are: Facebook, Relay and Email Account ID
	 * 
	 * @param httpSession
	 * @return
	 */
	private ExternalIdentityAuthenticationProviderAndId checkSessionForExternalIdFromKnownIdentityProviders(HttpSession httpSession) 
	{
		for(AuthenticationProviderType providerType : AuthenticationProviderType.values())
		{
			if(httpSession.getAttribute(providerType.getSessionIdentifierName()) != null)
			{
				return new ExternalIdentityAuthenticationProviderAndId(providerType, 
												httpSession.getAttribute(providerType.getSessionIdentifierName()).toString());
			}
		}
		return null;
	}
}
