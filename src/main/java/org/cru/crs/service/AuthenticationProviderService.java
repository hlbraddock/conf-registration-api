package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.UserEntity;
import org.sql2o.Sql2o;

public class AuthenticationProviderService
{

	Sql2o sql;
	@Inject
	public AuthenticationProviderService(EntityManager entityManager)
	{
		this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(AuthenticationProviderIdentityEntity.columnMappings);
	}

	/**
	 * Finds record in auth_provider_identities based on ID of that record in auth_provider_identities
	 * @param id
	 * @return
	 */
	public AuthenticationProviderIdentityEntity findAuthProviderIdentityById(UUID id)
	{
		return sql.createQuery("SELECT * FROM auth_provider_identities WHERE id = :id", false)
						.addParameter("id", id)
						.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}

	/**
	 * Finds record in auth_provider_identities based on the auth provider's id of the user.
	 *  ex: relay sso guid or facebook id.
	 * @param userAuthProviderId
	 * @return
	 */
	public AuthenticationProviderIdentityEntity findAuthProviderIdentityByUserAuthProviderId(String userAuthProviderId)
	{
		return sql.createQuery("SELECT * FROM auth_provider_identities WHERE user_auth_provider_id = :userAuthProviderId", false)
					.addParameter("userAuthProviderId", userAuthProviderId)
					.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}
	
	public AuthenticationProviderIdentityEntity findAuthProviderIdentityByAuthProviderUsernameAndType(String username, AuthenticationProviderType authenticationProviderType)
	{
		return sql.createQuery("SELECT * FROM auth_provider_identities WHERE username = :username AND auth_provider_name = :authProviderName", false)
					.addParameter("username", username)
					.addParameter("authProviderName", authenticationProviderType.getSessionIdentifierName())
					.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}

	/**
	 * Creates a new internal CRS App identity record along with a row for the authentication provider identity.
	 * The two rows will be associated by a foreign key relationship
	 */
	public void createIdentityAndAuthProviderRecords(AuthenticationProviderUser user)
	{
		UserEntity newUser = new UserEntity().setId(UUID.randomUUID());

		AuthenticationProviderIdentityEntity authProviderIdentityEntity = new AuthenticationProviderIdentityEntity();
		authProviderIdentityEntity.setId(UUID.randomUUID());
		authProviderIdentityEntity.setCrsId(newUser.getId());
		authProviderIdentityEntity.setUserAuthProviderId(user.getId());
		authProviderIdentityEntity.setAuthProviderUserAccessToken(user.getAccessToken());
		authProviderIdentityEntity.setAuthProviderName(user.getAuthenticationProviderType().name());
		authProviderIdentityEntity.setUsername(user.getUsername());
		authProviderIdentityEntity.setFirstName(user.getFirstName());
		authProviderIdentityEntity.setLastName(user.getLastName());
		
		sql.createQuery("INSERT INTO users(id) VALUES(:id)",false)
				.addParameter("id", newUser.getId())
				.executeUpdate();
		
//		sql.createQuery("INSERT INTO auth_provider_identities(id, crs_id, user_auth_provider_id, auth_provider_access_token) VALUES (:id, :crsId, :userAuthProviderId, :authProviderAccessToken", false)
//				.addParameter("id", authProviderIdentityEntity.getId())
//				.addParameter("crsId", value)
//		entityManager.persist(newUser);
//		entityManager.persist(authProviderIdentityEntity);
	}
	
	/**
	 * This method is used when changing a "No-Auth" provider type to an "Email-Auth" provider type
	 * @param authProviderId
	 * @param newAuthProviderType
	 */
	public AuthenticationProviderIdentityEntity updateAuthProviderType(String authProviderId, AuthenticationProviderType newAuthProviderType)
	{
//		AuthenticationProviderIdentityEntity authProviderEntity = findAuthProviderIdentityById(authProviderId);
//
//		if(authProviderEntity != null)
//		{
//			authProviderEntity.setAuthProviderName(newAuthProviderType.name());
//		}
//		return authProviderEntity;
		
		return null;
	}
}
