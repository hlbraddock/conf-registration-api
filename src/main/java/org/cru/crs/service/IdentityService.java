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

	public AuthenticationProviderIdentityEntity findAuthProviderIdentityBy(String externalIdentityId)
	{
		try
		{
			return entityManager.createQuery("SELECT extId FROM ExternalIdentityEntity extId " +
					"WHERE extId.idFromExternalIdentityProvider = :externalIdentityId", AuthenticationProviderIdentityEntity.class)
					.setParameter("externalIdentityId", externalIdentityId)
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
	 * Creates a new internal CRS App identity record along with a row for the external identity provider.
	 * The two rows will be associated by a foreign key relationship
	 * @param externalIdentityId
	 * @param externalIdentityProviderName
	 */
	public void createIdentityRecords(String authProviderId, AuthenticationProviderType authProviderType)
	{
		IdentityEntity identityEntity = new IdentityEntity();
		identityEntity.setId(UUID.randomUUID());
		
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = new AuthenticationProviderIdentityEntity();
		authProviderIdentityEntity.setId(UUID.randomUUID());
		authProviderIdentityEntity.setCrsApplicationUserId(identityEntity.getId());
		authProviderIdentityEntity.setIdFromExternalIdentityProvider(authProviderId.toLowerCase());/*necessary for search to work*/
		authProviderIdentityEntity.setExternalIdentityProviderName(authProviderType.name());
		
		entityManager.persist(identityEntity);
		entityManager.persist(authProviderIdentityEntity);
	}
}
