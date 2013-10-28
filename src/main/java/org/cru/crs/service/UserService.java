package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.model.UserEntity;
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
    public UserService(EntityManager em)
    {
    	this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(UserEntity.columnMappings);

    }

    public UserEntity fetchUserBy(UUID userId)
    {
        return sql.createQuery(userQueries.selectById(), false)
        			.addParameter("id", userId)
        			.executeAndFetchFirst(UserEntity.class);
    }

}
