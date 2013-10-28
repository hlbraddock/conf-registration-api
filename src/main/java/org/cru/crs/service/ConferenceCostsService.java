package org.cru.crs.service;

import java.util.UUID;

import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.queries.ConferenceCostsQueries;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.sql2o.Sql2o;

public class ConferenceCostsService
{
	Sql2o sql;

	ConferenceCostsQueries conferenceCostsQueries;
	
	public ConferenceCostsService()
	{
		this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(ConferenceCostsEntity.class));
		this.conferenceCostsQueries = new ConferenceCostsQueries();
	}
	
	public ConferenceCostsEntity fetchBy(UUID id)
	{
		return sql.createQuery(conferenceCostsQueries.selectById())
						.addParameter("id", id)
						.executeAndFetchFirst(ConferenceCostsEntity.class);
	}
	
	public void saveNew(ConferenceCostsEntity costs)
	{
		sql.createQuery(conferenceCostsQueries.insert())
				.addParameter("id", costs.getId())
				.addParameter("baseCost", costs.getConferenceBaseCost())
				.addParameter("minimumDeposit", costs.getMinimumDeposit())
				.addParameter("earlyRegistrationDiscount", costs.isEarlyRegistrationDiscount())
				.addParameter("earlyRegistrationAmount", costs.getEarlyRegistrationAmount())
				.addParameter("earlyRegistrationCutoff", costs.getEarlyRegistrationCutoff())
				.addParameter("acceptCreditCard", costs.isAcceptCreditCards())
				.addParameter("authnetId", costs.getAuthnetId())
				.addParameter("authnetToken", costs.getAuthnetToken())
				.executeUpdate();
	}
	
	public void update(ConferenceCostsEntity costs)
	{
		sql.createQuery(conferenceCostsQueries.update())
				.addParameter("id", costs.getId())
				.addParameter("baseCost", costs.getConferenceBaseCost())
				.addParameter("minimumDeposit", costs.getMinimumDeposit())
				.addParameter("earlyRegistrationDiscount", costs.isEarlyRegistrationDiscount())
				.addParameter("earlyRegistrationAmount", costs.getEarlyRegistrationAmount())
				.addParameter("earlyRegistrationCutoff", costs.getEarlyRegistrationCutoff())
				.addParameter("acceptCreditCard", costs.isAcceptCreditCards())
				.addParameter("authnetId", costs.getAuthnetId())
				.addParameter("authnetToken", costs.getAuthnetToken())
				.executeUpdate();
	}
}
