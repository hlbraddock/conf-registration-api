package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.model.SessionEntity;
import org.cru.crs.model.queries.SessionQueries;
import org.sql2o.Sql2o;

public class SessionService
{

	Sql2o sql;
	SessionQueries sessionQueries;
	
	@Inject
	public SessionService(Sql2o sql)
	{
		this.sql = sql;
		this.sessionQueries = new SessionQueries();
	}

	public SessionEntity getSessionByAuthCode(String authCode)
	{
		return sql.createQuery(sessionQueries.selectByAuthCode(), false)
					.addParameter("authCode", authCode)
					.setAutoDeriveColumnNames(true)
					.executeAndFetchFirst(SessionEntity.class);
	}

	public List<SessionEntity> fetchSessionsByUserAuthProviderId(UUID authProviderId)
	{
		return sql.createQuery(sessionQueries.selectByAuthProviderId())
					.addParameter("authProviderId", authProviderId)
					.setAutoDeriveColumnNames(true)
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