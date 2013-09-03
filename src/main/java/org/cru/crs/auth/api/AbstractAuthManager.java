package org.cru.crs.auth.api;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.utils.CrsProperties;

public abstract class AbstractAuthManager
{
    @Inject
    CrsProperties crsProperties;

    @Inject AuthenticationProviderService authenticationProviderService;

    protected String storeAuthCode(HttpServletRequest httpServletRequest, String authCode)
    {
        httpServletRequest.getSession().setAttribute("authCode", authCode);

        return authCode;
    }

    protected void persistIdentityAndAuthProviderRecordsIfNecessary(AuthenticationProviderUser user)
    {
        if(authenticationProviderService.findAuthProviderIdentityByAuthProviderId(user.getId()) == null)
        {
            authenticationProviderService.createIdentityAndAuthProviderRecords(user);
        }
    }

    protected CrsApplicationUser createCrsApplicationUser(AuthenticationProviderUser user)
	{
		AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderId(user.getId());
		
		return new CrsApplicationUser(authProviderEntity.getCrsUser().getId(), user.getAuthenticationProviderType(), user.getUsername());
	}
}
