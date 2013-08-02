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
	
	public UUID findCrsAppUserIdIdentityProviderIdIn(HttpSession httpSession)
	{
		/*look in the session to see if there's an external identity id we know about*/
		ExternalIdentityNameAndId externalIdentityInfo = checkSessionForExternalIdFromKnownIdentityProviders(httpSession);

		/*if not, then there's nothing further we can do*/
		if(externalIdentityInfo == null) return null;
		
		/*we have an id, now go look for the row in our database to hopefully find an internal user*/
		ExternalIdentityEntity externalIdentity = externalIdentityService.findExternalIdentityBy(externalIdentityInfo.identityId);
		
		
		if(externalIdentity == null)
		{
			/*oops, this person has not previously logged in.  let's create a row for them so next time we'll
			 * know who they are*/
			externalIdentityService.createIdentityRecords(externalIdentityInfo.identityId, 
																	externalIdentityInfo.identityProviderName);
			
			/*and fetch the information out. it wasn't elegant enough to just return it*/
			externalIdentity = externalIdentityService.findExternalIdentityBy(externalIdentityInfo.identityId);
		}
		
		/*finally return the CRS application id caller asked for*/
		return externalIdentity.getCrsApplicationUserId();
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
	private ExternalIdentityNameAndId checkSessionForExternalIdFromKnownIdentityProviders(HttpSession httpSession) 
	{
		if(httpSession.getAttribute("relaySsoGuid") != null)
		{
			return new ExternalIdentityNameAndId("Relay", ((UUID)httpSession.getAttribute("relaySsoGuid")).toString());
		}
		else if(httpSession.getAttribute("facebookId") != null)
		{
			return new ExternalIdentityNameAndId("Facebook", httpSession.getAttribute("facebookId").toString());
		}
		else if(httpSession.getAttribute("emailAccountId") != null)
		{
			return new ExternalIdentityNameAndId("Email", ((UUID)httpSession.getAttribute("emailAccountId")).toString());
		}
		return null;
	}
	
	private static class ExternalIdentityNameAndId
	{
		String identityProviderName;
		String identityId;
		
		public ExternalIdentityNameAndId(String identityProviderName,String identityId)
		{
			this.identityProviderName = identityProviderName;
			this.identityId = identityId;
		}
	}
}
