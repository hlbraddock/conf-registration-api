package org.cru.crs.service;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.model.SessionEntity;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.cru.crs.model.queries.SessionQueries;
import org.sql2o.Sql2o;

public class SessionService
{

	Sql2o sql;
	SessionQueries sessionQueries;
	
	@Inject
	public SessionService(EntityManager entityManager)
	{
		this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(SessionEntity.class));
		this.sessionQueries = new SessionQueries();
	}

	public SessionEntity getSessionByAuthCode(String authCode)
	{
		return sql.createQuery(sessionQueries.selectByAuthCode(), false)
					.addParameter("authCode", authCode)
					.executeAndFetchFirst(SessionEntity.class);
	}

	public List<SessionEntity> fetchSessionsByUserAuthProviderId(String	userAuthProviderId)
	{
		return sql.createQuery(sessionQueries.selectByAuthProviderId())
					.addParameter("authProviderId", userAuthProviderId)
					.executeAndFetch(SessionEntity.class);
	}

	public void create(SessionEntity sessionEntity)
	{
		sql.createQuery(sessionQueries.insert(), false)
					.addParameter("id", sessionEntity.getId())
					.addParameter("authProviderId", sessionEntity.getAuthProviderId())
					.addParameter("authCode", sessionEntity.getAuthCode())
					.addParameter("expiration", sessionEntity.getExpiration())
					.executeUpdate();
	}
}