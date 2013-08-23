package org.cru.crs.auth;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;

import com.google.common.base.Preconditions;

public class CrsUserService
{

	public CrsApplicationUser getUserFromSession(HttpSession httpSession, String authCode) throws UnauthorizedException
	{
		verifyAuthCode(httpSession, authCode);
		
		CrsApplicationUser loggedInUser = (CrsApplicationUser)httpSession.getAttribute(CrsApplicationUser.SESSION_OBJECT_NAME);
		
		if(loggedInUser == null)
		{
			throw new UnauthorizedException();
		}
		
		return loggedInUser;		
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
	
	private void verifyAuthCode(HttpSession session, String authCode) throws UnauthorizedException
	{
		String sessionAuthCode = (String)session.getAttribute("authCode");
		if(sessionAuthCode == null || authCode == null || !authCode.equals(sessionAuthCode))
		{
			throw new UnauthorizedException();
		}
	}
}
