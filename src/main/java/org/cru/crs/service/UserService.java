package org.cru.crs.service;

import org.cru.crs.model.UserEntity;
import org.cru.crs.model.queries.UserQueries;
import org.jboss.logging.Logger;
import org.sql2o.Connection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ryancarlson
 * Date: 9/23/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */

@RequestScoped
public class UserService {
	
	Connection sqlConnection;
	
	UserQueries userQueries = new UserQueries();
	
	/*Weld requires a default no args constructor to proxy this object*/
	public UserService(){ }
	
    @Inject
    public UserService(Connection sqlConnection) {
    	this.sqlConnection = sqlConnection;
    }

    public UserEntity getUserById(UUID userId) {
        return sqlConnection.createQuery(userQueries.selectById())
        						.addParameter("id", userId)
        						.setAutoDeriveColumnNames(true)
        						.executeAndFetchFirst(UserEntity.class);
    }
    
    public void createUser(UserEntity userToSave) {
    	if(userToSave.getId() == null) {
    		userToSave.setId(UUID.randomUUID());
    	}
    	
    	sqlConnection.createQuery(userQueries.insert())
    					.addParameter("id", userToSave.getId())
    					.addParameter("firstName", userToSave.getFirstName())
    					.addParameter("lastName", userToSave.getLastName())
    					.addParameter("emailAddress", userToSave.getEmailAddress())
    					.addParameter("phoneNumber", userToSave.getPhoneNumber())
    					.executeUpdate();
    }

	public void updateUser(UserEntity userEntity) {
		sqlConnection.createQuery(userQueries.update())
				.addParameter("id", userEntity.getId())
				.addParameter("firstName", userEntity.getFirstName())
				.addParameter("lastName", userEntity.getLastName())
				.addParameter("emailAddress", userEntity.getEmailAddress())
				.addParameter("phoneNumber", userEntity.getPhoneNumber())
				.executeUpdate();
	}

	public void deleteUser(UUID userId) {
		sqlConnection.createQuery(userQueries.delete())
				.addParameter("id", userId)
				.executeUpdate();
	}

	/**
	 * Convenience method(s)
	 */
	Logger logger = Logger.getLogger(getClass());

	public void deleteUserSwallowException(UUID userId)
	{
		try
		{
			deleteUser(userId);
		}
		catch(Exception exception)
		{
			logger.error("Could not delete user " + userId, exception);
		}
	}
}
