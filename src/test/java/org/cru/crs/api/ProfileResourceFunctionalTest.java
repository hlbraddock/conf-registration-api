package org.cru.crs.api;

import org.cru.crs.api.client.ProfileResourceClient;
import org.cru.crs.api.client.RegistrationResourceClient;
import org.cru.crs.api.model.ProfilePlus;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups="functional-tests")
public class ProfileResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";

	Environment environment = Environment.LOCAL;
	RegistrationResourceClient registrationClient;
	ProfileResourceClient profileResourceClient;

	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        profileResourceClient = ProxyFactory.create(ProfileResourceClient.class, restApiBaseUrl);
	}


	@Test(groups="functional-tests")
	public void getLoggedInUser()
	{
		ClientResponse<ProfilePlus> response = profileResourceClient.getProfile(UserInfo.AuthCode.TestUser);

		Assert.assertEquals(response.getStatus(), 200);

		ProfilePlus profilePlus = response.getEntity();

		Assert.assertEquals(profilePlus.getEmail(), UserInfo.Email.TestUser);
		Assert.assertEquals(profilePlus.getAuthProviderType(), AuthenticationProviderType.RELAY);
	}
}