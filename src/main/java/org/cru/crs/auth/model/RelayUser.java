package org.cru.crs.auth.model;

import org.cru.crs.auth.AuthenticationProviderType;

import edu.yale.its.tp.cas.client.CASReceipt;

public class RelayUser extends AuthenticationProviderUser
{
	
	public static RelayUser fromCasReceipt(CASReceipt casReceipt)
	{
		RelayUser relayUser = new RelayUser();
		
		relayUser.id = casReceipt.getAttributes().get("ssoGuid").toString().toLowerCase();
		relayUser.username = casReceipt.getAttributes().get("username").toString().toLowerCase();
		relayUser.firstName = casReceipt.getAttributes().get("firstName").toString().toLowerCase();
		relayUser.lastName = casReceipt.getAttributes().get("lastName").toString().toLowerCase();
		relayUser.authenticationProviderType = AuthenticationProviderType.RELAY;
		
		return relayUser;
	}

}
