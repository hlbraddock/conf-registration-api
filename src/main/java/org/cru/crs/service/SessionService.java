package org.cru.crs.service;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.model.SessionEntity;
import org.sql2o.Sql2o;

public class SessionService
{

	Sql2o sql;
	
	@Inject
	public SessionService(EntityManager entityManager)
	{
		this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(SessionEntity.columnMappings);
	}

	public SessionEntity getSessionByAuthCode(String authCode)
	{
		return sql.createQuery("SELECT * FROM sessions WHERE auth_code = :authCode", false)
					.addParameter("authCode", authCode)
					.executeAndFetchFirst(SessionEntity.class);
	}

	public List<SessionEntity> fetchSessionsByUserAuthProviderId(String	userAuthProviderId)
	{
		return null;
	}

	public void create(SessionEntity sessionEntity)
	{
		sql.createQuery("INSERT INTO sessions(id, auth_provider_id, auth_code, expiration) VALUES(:id, :authProviderId, :authCode, :expiration)", false)
					.addParameter("id", sessionEntity.getId())
					.addParameter("authProviderId", sessionEntity.getAuthProviderId())
					.addParameter("authCode", sessionEntity.getAuthCode())
					.addParameter("expiration", sessionEntity.getExpiration())
					.executeUpdate();
	}
}