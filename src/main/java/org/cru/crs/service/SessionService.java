package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.SessionEntity;
import org.cru.crs.model.queries.SessionQueries;
import org.sql2o.Connection;

@RequestScoped
public class SessionService
{
	org.sql2o.Connection sqlConnection;

	SessionQueries sessionQueries;
	
	/*Weld requires a default no args constructor to proxy this object*/
	public SessionService(){ }
	
	@Inject
	public SessionService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
		this.sessionQueries = new SessionQueries();
	}

	public SessionEntity getSessionByAuthCode(String authCode)
	{
		return sqlConnection.createQuery(sessionQueries.selectByAuthCode(), false)
								.addParameter("authCode", authCode)
								.setAutoDeriveColumnNames(true)
								.executeAndFetchFirst(SessionEntity.class);
	}

	public List<SessionEntity> fetchSessionsByUserAuthProviderId(UUID authProviderId)
	{
		return sqlConnection.createQuery(sessionQueries.selectByAuthProviderId())
								.addParameter("authProviderId", authProviderId)
								.setAutoDeriveColumnNames(true)
								.executeAndFetch(SessionEntity.class);
	}

	public void create(SessionEntity sessionEntity)
	{
		sqlConnection.createQuery(sessionQueries.insert(), false)
						.addParameter("id", sessionEntity.getId())
						.addParameter("authProviderId", sessionEntity.getAuthProviderId())
						.addParameter("authCode", sessionEntity.getAuthCode())
						.addParameter("expiration", sessionEntity.getExpiration())
						.executeUpdate();
	}

	public void update(SessionEntity sessionEntity)
	{
		sqlConnection.createQuery(sessionQueries.update(), false)
				.addParameter("id", sessionEntity.getId())
				.addParameter("authProviderId", sessionEntity.getAuthProviderId())
				.addParameter("authCode", sessionEntity.getAuthCode())
				.addParameter("expiration", sessionEntity.getExpiration())
				.executeUpdate();
	}
}