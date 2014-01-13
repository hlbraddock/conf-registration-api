package org.cru.crs.service;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.queries.AuthenticationProviderQueries;
import org.sql2o.Connection;

@RequestScoped
public class AuthenticationProviderService
{
	org.sql2o.Connection sqlConnection;
	
	AuthenticationProviderQueries authenticationProviderQueries;
	
	/*Weld requires a default no args constructor to proxy this object*/
	public AuthenticationProviderService(){ }
	
	@Inject
	public AuthenticationProviderService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
		
		this.authenticationProviderQueries = new AuthenticationProviderQueries();
	}

	/**
	 * Finds record in auth_provider_identities based on ID of that record in auth_provider_identities
	 * @param id
	 * @return
	 */
	public AuthenticationProviderIdentityEntity findAuthProviderIdentityById(UUID id)
	{
		return sqlConnection.createQuery(authenticationProviderQueries.selectById())
								.addParameter("id", id)
								.setAutoDeriveColumnNames(true)
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
		return sqlConnection.createQuery(authenticationProviderQueries.selectByUserAuthProviderId())
					.addParameter("userAuthProviderId", userAuthProviderId)
					.setAutoDeriveColumnNames(true)
					.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}
	
	public AuthenticationProviderIdentityEntity findAuthProviderIdentityByAuthProviderUsernameAndType(String username, AuthenticationProviderType authenticationProviderType)
	{
		return sqlConnection.createQuery(authenticationProviderQueries.selectByUsernameAuthProviderName())
								.addParameter("username", username)
								.addParameter("authProviderName", authenticationProviderType.name())
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}

	public void createAuthProviderRecord(AuthenticationProviderIdentityEntity authenticationProviderIdentityEntity)
	{
		sqlConnection.createQuery(authenticationProviderQueries.insert(),false)
				.addParameter("id", authenticationProviderIdentityEntity.getId())
				.addParameter("crsId", authenticationProviderIdentityEntity.getCrsId())
				.addParameter("userAuthProviderId", authenticationProviderIdentityEntity.getUserAuthProviderId())
				.addParameter("authProviderUserAccessToken", authenticationProviderIdentityEntity.getAuthProviderUserAccessToken())
				.addParameter("authProviderName", authenticationProviderIdentityEntity.getAuthProviderName())
				.addParameter("username", authenticationProviderIdentityEntity.getUsername())
				.addParameter("firstName", authenticationProviderIdentityEntity.getFirstName())
				.addParameter("lastName", authenticationProviderIdentityEntity.getLastName())
				.executeUpdate();
	}

	/**
	 * This method is used when changing a "No-Auth" provider type to an "Email-Auth" provider type
	 * @param authProviderId
	 * @param newAuthProviderType
	 */
	public void updateAuthProviderType(String authProviderId, AuthenticationProviderType newAuthProviderType)
	{
		sqlConnection.createQuery(authenticationProviderQueries.updateAuthProviderType())
						.addParameter("authProviderName", newAuthProviderType.getSessionIdentifierName())
						.addParameter("userAuthProviderId", authProviderId)
						.executeUpdate();
	}
}
