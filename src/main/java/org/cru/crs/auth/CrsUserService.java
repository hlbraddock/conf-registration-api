package org.cru.crs.auth;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.service.IdentityService;

import com.google.common.base.Preconditions;

public class CrsUserService
{
	IdentityService authProviderIdentityService;
	
	@Inject
	public CrsUserService(IdentityService authProviderIdentityService)
	{
		this.authProviderIdentityService = authProviderIdentityService;
	}
	
	public CrsApplicationUser buildCrsApplicationUserBasedOnDataIn(HttpSession httpSession)
	{
		/*look in the session to see if there's an external identity id we know about*/
		IdentityAuthenticationProviderAndId authProviderIdentityInfo = checkSessionForIdFromKnownIdentityProviders(httpSession);
		
		/*if not, then there's nothing further we can do*/
		if(authProviderIdentityInfo == null) return null; //TODO: have this throw an exception
		
		/*we have an id, now go look for the row in our database to hopefully find an internal user*/
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = authProviderIdentityService.findAuthProviderIdentityBy(authProviderIdentityInfo.authProviderId);
		
		if(authProviderIdentityEntity == null)
		{
			/*oops, this person has not previously logged in.  let's create a row for them so next time we'll
			 * know who they are*/
			authProviderIdentityService.createIdentityRecords(authProviderIdentityInfo.authProviderId,authProviderIdentityInfo.authProviderType);
			/*and fetch the information out. it wasn't elegant enough to just return it*/
			authProviderIdentityEntity = authProviderIdentityService.findAuthProviderIdentityBy(authProviderIdentityInfo.authProviderId);
		}
		
		/*finally return the CRS application id caller asked for*/
		return new CrsApplicationUser(authProviderIdentityEntity.getCrsApplicationUserId(), 
										authProviderIdentityInfo.authProviderId, 
										authProviderIdentityInfo.authProviderType);
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
	private IdentityAuthenticationProviderAndId checkSessionForIdFromKnownIdentityProviders(HttpSession httpSession) 
	{
		for(AuthenticationProviderType providerType : AuthenticationProviderType.values())
		{
			if(httpSession.getAttribute(providerType.getSessionIdentifierName()) != null)
			{
				return new IdentityAuthenticationProviderAndId(providerType, 
												httpSession.getAttribute(providerType.getSessionIdentifierName()).toString());
			}
		}
		return null;
	}
	
	private static class IdentityAuthenticationProviderAndId
	{
		AuthenticationProviderType authProviderType;
		String authProviderId;
		
		public IdentityAuthenticationProviderAndId(AuthenticationProviderType authProviderType, String authProviderId)
		{
			this.authProviderType = authProviderType;
			this.authProviderId = authProviderId;
		}
	}
}
