package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

public class BasicNoAuthUser extends AuthenticationProviderUser
{

	public static BasicNoAuthUser fromCode(String code)
	{
		BasicNoAuthUser user = new BasicNoAuthUser();
		
		user.id = code;
		user.authentcationProviderType = AuthenticationProviderType.NONE;
		
		return user;
	}
}
