package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public class EmailAccountUser extends AuthenticationProviderUser
{
	public static EmailAccountUser fromCode(String code)
	{
		EmailAccountUser user = new EmailAccountUser();
		
		user.id = code;
		user.authentcationProviderType = AuthenticationProviderType.EMAIL_ACCOUNT;
		
		return user;
	}
}
