package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public class BasicNoAuthUser extends AuthenticationProviderUser
{

	public static BasicNoAuthUser fromAuthIdAndEmail(String authId, String emailAddress)
	{
		BasicNoAuthUser user = new BasicNoAuthUser();
		
		user.id = authId;
		user.username = emailAddress;
		user.authentcationProviderType = AuthenticationProviderType.NONE;
		
		return user;
	}
}
