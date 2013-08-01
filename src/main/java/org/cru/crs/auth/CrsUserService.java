package org.cru.crs.auth;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.cru.crs.model.ExternalIdentityEntity;
import org.cru.crs.service.IdentityService;

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
		ExternalIdentityNameAndId externalIdentityInfo = checkSessionForExternalIdFromKnownIdentityProviders(httpSession);
		
		if(externalIdentityInfo == null) return null;
		
		ExternalIdentityEntity externalIdentity = externalIdentityService.findExternalIdentityBy(externalIdentityInfo.identityId);
		
		if(externalIdentity == null)
		{
			externalIdentityService.createIdentityRecords(externalIdentityInfo.identityId, 
																	externalIdentityInfo.identityProviderName);
		}
		
		externalIdentity = externalIdentityService.findExternalIdentityBy(externalIdentityInfo.identityId);
		
		return externalIdentity.getCrsApplicationUserId();
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
