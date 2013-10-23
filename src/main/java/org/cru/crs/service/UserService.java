package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.model.UserEntity;
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
    @Inject
    public UserService(EntityManager em)
    {
    	this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(UserEntity.columnMappings);

    }

    public UserEntity fetchUserBy(UUID userId)
    {
        return sql.createQuery("SELECT * FROM users WHERE id = :id", false)
        			.addParameter("id", userId)
        			.executeAndFetchFirst(UserEntity.class);
    }

}
