package org.cru.crs.utils;

import java.util.UUID;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.CrsApplicationUser;

public class UserInfo
{
	public static class Id
	{
		public static UUID Ryan = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f9883c1");
		public static UUID Email = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f988343");
		public static UUID TestUser = UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898");
	}

	public static class AuthCode
	{
		public static String Ryan = "fd33c83b97b59dc3884454b7c2b861db03d5399c";
		public static String Email = "488aca23cecd6e5b8ac406bf74a46723dd853273";
		public static String Expired = "123aca23cecd6e5b8ac406bf74a46723dd853273";
		public static String TestUser = "11eac4a91ccb730509cd82d822b5b4dd202de7ff";
	}
	
	public static class AuthProviderId
	{
		public static UUID Ryan = UUID.fromString("f8f8c217-f977-4503-b3b3-85016f9883c1");
		public static UUID Email = UUID.fromString("f8f8c217-f977-4503-b3b3-85016f988342");
		public static UUID TestUser = UUID.fromString("36f19114-f833-4a26-b7ba-b67052b68cea");
	}
	
	public static class Users
	{
		public static CrsApplicationUser TestUser = new CrsApplicationUser(UserInfo.Id.TestUser,AuthenticationProviderType.RELAY, "crs.testuser@crue.org");
		public static CrsApplicationUser Ryan = new CrsApplicationUser(UserInfo.Id.Ryan,AuthenticationProviderType.RELAY, "ryan.t.carlson@cru.org");
		//this Id is used throughout tests, so even though EMAIL is no longer a valid type, we should keep the ID
		public static CrsApplicationUser Email = new CrsApplicationUser(UserInfo.Id.Email,AuthenticationProviderType.NONE, "email.account@cru.org");
	}
}
