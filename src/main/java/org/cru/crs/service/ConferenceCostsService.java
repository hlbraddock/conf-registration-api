package org.cru.crs.service;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.queries.ConferenceCostsQueries;
import org.sql2o.Connection;

@RequestScoped
public class ConferenceCostsService
{
	org.sql2o.Connection sqlConnection;

	ConferenceCostsQueries conferenceCostsQueries;
	
	/*required for Weld*/
	public ConferenceCostsService(){ }
	
	@Inject
	public ConferenceCostsService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
		this.conferenceCostsQueries = new ConferenceCostsQueries();
	}
	
	public ConferenceCostsEntity fetchBy(UUID id)
	{
		return sqlConnection.createQuery(conferenceCostsQueries.selectById(),false)
							.addParameter("id", id)
							.setAutoDeriveColumnNames(true)
							.executeAndFetchFirst(ConferenceCostsEntity.class);
	}
	
	public void saveNew(ConferenceCostsEntity costs)
	{
		sqlConnection.createQuery(conferenceCostsQueries.insert(),false)
						.addParameter("id", costs.getId())
						.addParameter("baseCost", costs.getBaseCost())
						.addParameter("minimumDeposit", costs.getMinimumDeposit())
						.addParameter("earlyRegistrationDiscount", costs.isEarlyRegistrationDiscount())
						.addParameter("earlyRegistrationAmount", costs.getEarlyRegistrationAmount())
						.addParameter("earlyRegistrationCutoff", costs.getEarlyRegistrationCutoff())
						.addParameter("acceptCreditCards", costs.isAcceptCreditCards())
						.addParameter("authnetId", costs.getAuthnetId())
						.addParameter("authnetToken", costs.getAuthnetToken())
						.executeUpdate();
	}
	
	public void update(ConferenceCostsEntity costs)
	{
		sqlConnection.createQuery(conferenceCostsQueries.update(),false)
						.addParameter("id", costs.getId())
						.addParameter("baseCost", costs.getBaseCost())
						.addParameter("minimumDeposit", costs.getMinimumDeposit())
						.addParameter("earlyRegistrationDiscount", costs.isEarlyRegistrationDiscount())
						.addParameter("earlyRegistrationAmount", costs.getEarlyRegistrationAmount())
						.addParameter("earlyRegistrationCutoff", costs.getEarlyRegistrationCutoff())
						.addParameter("acceptCreditCards", costs.isAcceptCreditCards())
						.addParameter("authnetId", costs.getAuthnetId())
						.addParameter("authnetToken", costs.getAuthnetToken())
						.executeUpdate();
	}
}
