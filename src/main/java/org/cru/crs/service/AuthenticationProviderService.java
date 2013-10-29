package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.model.AuthenticationProviderUser;
import org.cru.crs.model.AuthenticationProviderIdentityEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.model.queries.AuthenticationProviderQueries;
import org.sql2o.Sql2o;

public class AuthenticationProviderService
{

	Sql2o sql;
	UserService userService;
	
	AuthenticationProviderQueries authenticationProviderQueries;
	
	@Inject
	public AuthenticationProviderService(Sql2o sql, UserService userService, AuthenticationProviderQueries authenticationProviderQueries)
	{
		this.sql = sql;
		this.sql.setDefaultColumnMappings(AuthenticationProviderIdentityEntity.columnMappings);
		
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
		return sql.createQuery(authenticationProviderQueries.selectById(), false)
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
		return sql.createQuery(authenticationProviderQueries.selectByUserAuthProviderId(), false)
					.addParameter("userAuthProviderId", userAuthProviderId)
					.executeAndFetchFirst(AuthenticationProviderIdentityEntity.class);
	}
	
	public AuthenticationProviderIdentityEntity findAuthProviderIdentityByAuthProviderUsernameAndType(String username, AuthenticationProviderType authenticationProviderType)
	{
		return sql.createQuery(authenticationProviderQueries.selectByUsernameAuthProviderName(), false)
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
		
		userService.createUser(newUser);
		
		sql.createQuery(authenticationProviderQueries.insert(),false)
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
	
	/**
	 * This method is used when changing a "No-Auth" provider type to an "Email-Auth" provider type
	 * @param authProviderId
	 * @param newAuthProviderType
	 */
	public AuthenticationProviderIdentityEntity updateAuthProviderType(String authProviderId, AuthenticationProviderType newAuthProviderType)
	{
		sql.createQuery(authenticationProviderQueries.updateAuthProviderType())
				.addParameter("authProviderName", newAuthProviderType.getSessionIdentifierName())
				.addParameter("userAuthProviderId", authProviderId)
				.executeUpdate();
		
		return findAuthProviderIdentityByUserAuthProviderId(authProviderId);
	}
}
