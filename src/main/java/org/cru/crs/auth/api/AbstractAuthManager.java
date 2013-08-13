package org.cru.crs.auth.api;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.service.IdentityService;
import org.cru.crs.utils.AuthCodeGenerator;
import org.cru.crs.utils.CrsProperties;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractAuthManager
{
    @Inject
    CrsProperties crsProperties;

    @Inject IdentityService authenticationProviderService;

    protected String generateAndStoreAuthCodeInSession(HttpServletRequest httpServletRequest)
    {
        String authCode = AuthCodeGenerator.generate();

        httpServletRequest.getSession().setAttribute("authCode", authCode);

        return authCode;
    }

    protected void persistIdentityAndAuthProviderRecordsIfNecessary(String id, AuthenticationProviderType authenticationProviderType)
    {
        if(authenticationProviderService.findAuthProviderIdentityByAuthProviderId(id) == null)
        {
            authenticationProviderService.createIdentityAndAuthProviderRecords(id, authenticationProviderType);
        }
    }

    protected CrsApplicationUser createCrsApplicationUser(String id, AuthenticationProviderType authenticationProviderType)
	{
		AuthenticationProviderIdentityEntity authProviderEntity = authenticationProviderService.findAuthProviderIdentityByAuthProviderId(id);
		
		return new CrsApplicationUser(authProviderEntity.getCrsApplicationUserId(), id, authenticationProviderType);
	}
}
