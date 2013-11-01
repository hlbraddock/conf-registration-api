package org.cru.crs.service;

import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.queries.ConferenceCostsQueries;
import org.sql2o.Sql2o;

public class ConferenceCostsService
{
	Sql2o sql;

	ConferenceCostsQueries conferenceCostsQueries;
	
	@Inject
	public ConferenceCostsService(Sql2o sql)
	{
		this.sql = sql;
		this.conferenceCostsQueries = new ConferenceCostsQueries();
	}
	
	public ConferenceCostsEntity fetchBy(UUID id)
	{
		return sql.createQuery(conferenceCostsQueries.selectById(),false)
						.addParameter("id", id)
						.setAutoDeriveColumnNames(true)
						.executeAndFetchFirst(ConferenceCostsEntity.class);
	}
	
	public void saveNew(ConferenceCostsEntity costs)
	{
		sql.createQuery(conferenceCostsQueries.insert(),false)
				.addParameter("id", costs.getId())
				.addParameter("baseCost", costs.getBaseCost())
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
		sql.createQuery(conferenceCostsQueries.update(),false)
				.addParameter("id", costs.getId())
				.addParameter("baseCost", costs.getBaseCost())
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
