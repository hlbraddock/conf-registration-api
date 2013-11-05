package org.cru.crs.model.queries;

public class ConferenceCostsQueries implements BasicQueries 
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM conference_costs WHERE id = :id"; 
	}

	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE conference_costs SET ")
				.append("base_cost = :baseCost,")
				.append("minimum_deposit = :minimumDeposit,")
				.append("early_registration_discount = :earlyRegistrationDiscount,")
				.append("early_registration_amount = :earlyRegistrationAmount,")
				.append("early_registration_cutoff = :earlyRegistrationCutoff,")
				.append("accept_credit_cards = :acceptCreditCards,")
				.append("authnet_id = :authnetId,")
				.append("authnet_token = :authnetToken")
				.append(" WHERE id = :id");
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO conference_costs(")
				.append("id,")
				.append("base_cost,")
				.append("minimum_deposit,")
				.append("early_registration_discount,")
				.append("early_registration_amount,")
				.append("early_registration_cutoff,")
				.append("accept_credit_cards,")
				.append("authnet_id,")
				.append("authnet_token")
				.append(") VALUES (")
				.append(":id,")
				.append(":baseCost,")
				.append(":minimumDeposit,")
				.append(":earlyRegistrationDiscount,")
				.append(":earlyRegistrationAmount,")
				.append(":earlyRegistrationCutoff,")
				.append(":acceptCreditCards,")
				.append(":authnetId,")
				.append(":authnetToken")
				.append(")");
		
		return query.toString();
				
	}

	@Override
	public String delete()
	{
		return "DELETE FROM conference_costs WHERE id = :id";
	}

}
