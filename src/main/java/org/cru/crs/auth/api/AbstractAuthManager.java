package org.cru.crs.auth.api;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.service.AuthenticationProviderService;
import org.cru.crs.utils.CrsProperties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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

    protected void persistIdentityAndAuthProviderRecordsIfNecessary(String id, 
    																	AuthenticationProviderType authenticationProviderType, 
    																	String authProviderUsername)
    {
        if(authenticationProviderService.findAuthProviderIdentityByAuthProviderId(id) == null)
        {
            authenticationProviderService.createIdentityAndAuthProviderRecords(id, authenticationProviderType, authProviderUsername);
        }
    }

    protected CrsApplicationUser createCrsApplicationUser(String id, AuthenticationProviderType authenticationProviderType, String authProviderUsername)
	{
		AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderId(id);
		
		return new CrsApplicationUser(authProviderEntity.getCrsUser().getId(), authenticationProviderType, authProviderUsername);
	}
}
