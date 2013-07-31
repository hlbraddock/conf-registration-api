package org.cru.crs.auth;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

public class CrsUserService
{
	EntityManager entityManager;
	
	@Inject
	public CrsUserService(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	public UUID findCrsAppUserIdIdentityProviderIdIn(HttpSession httpSession)
	{
		UUID relaySsoGuid = (UUID)httpSession.getAttribute("relaySsoGuid");
		
		/* do a theoretical lookup on Relay GUID here... */
		/* do a theoretical lookup on Facebook ID here... */
		/* do a theoretical lookup on Email AddressID here... */
		
		//TODO: return the eventual CRS user ID once I get that far, but for Proof of Concept, return the relay ID
		return relaySsoGuid;
	}
}
