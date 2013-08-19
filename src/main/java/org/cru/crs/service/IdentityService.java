package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.IdentityEntity;

public class IdentityService
{

	EntityManager entityManager;

	@Inject
	public IdentityService(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	public AuthenticationProviderIdentityEntity findAuthProviderIdentityByAuthProviderId(String authenticationProviderId)
	{
		try
		{
			return entityManager.createQuery("SELECT ape FROM AuthenticationProviderIdentityEntity ape " +
					"WHERE ape.authenticationProviderId = :authenticationProviderId", AuthenticationProviderIdentityEntity.class)
					.setParameter("authenticationProviderId", authenticationProviderId)
					.getSingleResult();
		}
		catch(NoResultException nre)
		{
			/* silly JPA, this is no reason to throw an exception and make calling code handle it. it just means there is no
			 * record matching my criteria. it's the same as asking a yes/no question and throwing an exception when the answer
			 * is 'no'.  okay, i'll get off my soapbox now, but really.... */
			return null;
		}
	}
	
	/**
	 * Creates a new internal CRS App identity record along with a row for the authentication provider identity.
	 * The two rows will be associated by a foreign key relationship
	 * @param externalIdentityId
	 * @param externalIdentityProviderName
	 */
	public void createIdentityAndAuthProviderRecords(String authProviderId, AuthenticationProviderType authProviderType)
	{
		IdentityEntity identityEntity = new IdentityEntity();
		identityEntity.setId(UUID.randomUUID());
		
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = new AuthenticationProviderIdentityEntity();
		authProviderIdentityEntity.setId(UUID.randomUUID());
		authProviderIdentityEntity.setCrsApplicationUserId(identityEntity.getId());
		authProviderIdentityEntity.setAuthenticationProviderId(authProviderId);
		authProviderIdentityEntity.setAuthenticationProviderName(authProviderType.name());
		
		entityManager.persist(identityEntity);
		entityManager.persist(authProviderIdentityEntity);
	}
	
	/**
	 * This method is used when changing a "No-Auth" provider type to an "Email-Auth" provider type
	 * @param authProviderId
	 * @param newAuthProviderType
	 */
	public void updateAuthProviderType(String authProviderId, AuthenticationProviderType newAuthProviderType)
	{
		AuthenticationProviderIdentityEntity authProviderEntity = findAuthProviderIdentityByAuthProviderId(authProviderId);
		authProviderEntity.setAuthenticationProviderName(newAuthProviderType.name());
	}
}
