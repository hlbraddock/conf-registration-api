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
		return "UPDATE conference_costs SET  " +
				 "base_cost = :baseCost, " +
				 "minimum_deposit = :minimumDeposit, " +
				 "early_registration_discount = :earlyRegistrationDiscount, " +
				 "early_registration_amount = :earlyRegistrationAmount, " +
				 "early_registration_cutoff = :earlyRegistrationCutoff, " +
				 "accept_credit_cards = :acceptCreditCards, " +
				 "authnet_id = :authnetId, " +
				 "authnet_token = :authnetToken " +
				 " WHERE id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO conference_costs(id, base_cost, minimum_deposit, early_registration_discount, early_registration_amount, early_registration_cutoff, " +
				"accept_credit_cards, authnet_id, authnet_token) " + 
				"VALUES (:id, :baseCost, :minimumDeposit, :earlyRegistrationDiscount, :earlyRegistrationAmount, :earlyRegistrationCutoff, " +
				":acceptCreditCards, :authnetId, :authnetToken)";
				
	}

	@Override
	public String delete()
	{
		return "DELETE FROM conference_costs WHERE id = :id";
	}

}
