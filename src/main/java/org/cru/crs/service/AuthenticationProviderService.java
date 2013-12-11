package org.cru.crs.service;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.model.queries.AuthenticationProviderQueries;
import org.sql2o.Connection;

@RequestScoped
public class AuthenticationProviderService
{
	org.sql2o.Connection sqlConnection;
	
	UserService userService;
	
	AuthenticationProviderQueries authenticationProviderQueries;
	
	/*required for Weld*/
	public AuthenticationProviderService(){ }
	
	@Inject
	public AuthenticationProviderService(Connection sqlConnection, UserService userService)
	{
		this.sqlConnection = sqlConnection;
		
		this.userService = userService;
		
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
								.addParameter("authProviderName", authenticationProviderType.getSessionIdentifierName())
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}

	/**
	 * Creates a new internal CRS App identity record along with a row for the authentication provider identity.
	 * The two rows will be associated by a foreign key relationship
	 */
	public void createIdentityAndAuthProviderRecords(AuthenticationProviderUser user)
	{
		UserEntity newUser = new UserEntity().setId(UUID.randomUUID());
		
		setUserFields(user, newUser);
		
		AuthenticationProviderIdentityEntity authProviderIdentityEntity = new AuthenticationProviderIdentityEntity();
		authProviderIdentityEntity.setId(UUID.randomUUID());
		authProviderIdentityEntity.setCrsId(newUser.getId());
		authProviderIdentityEntity.setUserAuthProviderId(user.getId());
		authProviderIdentityEntity.setAuthProviderUserAccessToken(user.getAccessToken());
		authProviderIdentityEntity.setAuthProviderName(user.getAuthenticationProviderType().name());
		authProviderIdentityEntity.setUsername(user.getUsername());
		authProviderIdentityEntity.setFirstName(user.getFirstName());
		authProviderIdentityEntity.setLastName(user.getLastName());
		
		userService.createUser(newUser);
		
		sqlConnection.createQuery(authenticationProviderQueries.insert(),false)
						.addParameter("id", authProviderIdentityEntity.getId())
						.addParameter("crsId", authProviderIdentityEntity.getCrsId())
						.addParameter("userAuthProviderId", authProviderIdentityEntity.getUserAuthProviderId())
						.addParameter("authProviderUserAccessToken", authProviderIdentityEntity.getAuthProviderUserAccessToken())
						.addParameter("authProviderName", authProviderIdentityEntity.getAuthProviderName())
						.addParameter("username", authProviderIdentityEntity.getUsername())
						.addParameter("firstName", authProviderIdentityEntity.getFirstName())
						.addParameter("lastName", authProviderIdentityEntity.getLastName())
						.executeUpdate();
	}

	private void setUserFields(AuthenticationProviderUser user, UserEntity newUser)
	{
		if(user.getAuthenticationProviderType() == AuthenticationProviderType.RELAY) newUser.setEmailAddress(user.getUsername());
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
	}
	
	/**
	 * This method is used when changing a "No-Auth" provider type to an "Email-Auth" provider type
	 * @param authProviderId
	 * @param newAuthProviderType
	 */
	public AuthenticationProviderIdentityEntity updateAuthProviderType(String authProviderId, AuthenticationProviderType newAuthProviderType)
	{
		sqlConnection.createQuery(authenticationProviderQueries.updateAuthProviderType())
						.addParameter("authProviderName", newAuthProviderType.getSessionIdentifierName())
						.addParameter("userAuthProviderId", authProviderId)
						.executeUpdate();
		
		return findAuthProviderIdentityByUserAuthProviderId(authProviderId);
	}
}
