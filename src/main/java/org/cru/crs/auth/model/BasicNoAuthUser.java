package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public class BasicNoAuthUser extends AuthenticationProviderUser
{
	public static BasicNoAuthUser fromAuthIdAndEmail(String authId)
	{
		BasicNoAuthUser user = new BasicNoAuthUser();
		
		user.id = authId;
		user.authenticationProviderType = AuthenticationProviderType.NONE;
		
		return user;
	}
}
