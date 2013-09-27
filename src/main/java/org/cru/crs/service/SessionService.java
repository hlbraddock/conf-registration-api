package org.cru.crs.service;

import org.cru.crs.model.SessionEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class SessionService
{
	EntityManager entityManager;

	@Inject
	public SessionService(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	public SessionEntity getSessionByAuthCode(String authCode)
	{
		try
		{
			return entityManager.createQuery("SELECT session FROM SessionEntity session " +
					"WHERE session.authCode = :authCode", SessionEntity.class)
					.setParameter("authCode", authCode)
					.getSingleResult();
		}
		catch(NoResultException nre)
		{
			/* silly JPA, this is no reason to throw an exception and make calling code handle it. it just means there is no
			 * record matching my criteria. it's the same as asking a yes/no question and throwing an exception when the answer
			 * is 'no'.  okay, i'll get off my soapbox now, but really.... */
			return null;
		}
	}

	public void create(SessionEntity sessionEntity)
	{
		entityManager.persist(sessionEntity);
	}
}
