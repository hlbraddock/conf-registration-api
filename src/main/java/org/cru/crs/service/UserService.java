package org.cru.crs.service;

import org.cru.crs.model.UserEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ryancarlson
 * Date: 9/23/13
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserService
{
    EntityManager em;

    @Inject
    public UserService(EntityManager em)
    {
        this.em = em;

    }

    public UserEntity fetchUserBy(UUID userId)
    {
        return em.find(UserEntity.class, userId);
    }

}
