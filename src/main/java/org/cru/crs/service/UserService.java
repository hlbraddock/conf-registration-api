package org.cru.crs.service;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.UserEntity;
import org.cru.crs.model.queries.UserQueries;
import org.sql2o.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: ryancarlson
 * Date: 9/23/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */

@RequestScoped
public class UserService
{
	org.sql2o.Connection sqlConnection;
	
	UserQueries userQueries = new UserQueries();
	
	/*Weld requires a default no args constructor to proxy this object*/
	public UserService(){ }
	
    @Inject
    public UserService(Connection sqlConnection)
    {
    	this.sqlConnection = sqlConnection;
    }

    public UserEntity fetchUserBy(UUID userId)
    {
        return sqlConnection.createQuery(userQueries.selectById())
        						.addParameter("id", userId)
        						.setAutoDeriveColumnNames(true)
        						.executeAndFetchFirst(UserEntity.class);
    }

    public void createUser(UserEntity userToSave)
    {
    	if(userToSave.getId() == null)
    	{
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
}
