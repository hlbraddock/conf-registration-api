package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public class EmailAccountUser extends AuthenticationProviderUser
{
	public static EmailAccountUser fromAuthIdAndEmail(String authId, String emailAddress)
	{
		EmailAccountUser user = new EmailAccountUser();
		
		user.id = authId;
		user.username = emailAddress;
		user.authenticationProviderType = AuthenticationProviderType.EMAIL_ACCOUNT;
		
		return user;
	}
}
