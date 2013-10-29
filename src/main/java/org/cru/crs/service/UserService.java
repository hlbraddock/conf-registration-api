package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.model.UserEntity;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.cru.crs.model.queries.UserQueries;
import org.sql2o.Sql2o;

/**
 * Created with IntelliJ IDEA.
 * User: ryancarlson
 * Date: 9/23/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserService
{
    
	Sql2o sql;
	
	UserQueries userQueries = new UserQueries();
	
    @Inject
    public UserService(Sql2o sql)
    {
    	this.sql = sql;
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(UserEntity.class));
    }

    public UserEntity fetchUserBy(UUID userId)
    {
        return sql.createQuery(userQueries.selectById(), false)
        			.addParameter("id", userId)
        			.executeAndFetchFirst(UserEntity.class);
    }

    public void createUser(UserEntity userToSave)
    {
    	if(userToSave.getId() == null)
    	{
    		userToSave.setId(UUID.randomUUID());
    	}
    	
    	sql.createQuery(userQueries.insert())
    			.addParameter(":id", userToSave.getId())
    			.addParameter(":firstName", userToSave.getFirstName())
    			.addParameter("lastName", userToSave.getLastName())
    			.addParameter("emailAddress", userToSave.getEmailAddress())
    			.addParameter(":phoneNumber", userToSave.getPhoneNumber())
    			.executeUpdate();
    }
}
