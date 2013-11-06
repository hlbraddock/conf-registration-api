package org.cru.crs.model.queries;

public class PaymentQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM payments WHERE id = :id";
	}
	
	public String selectAllForRegistration()
	{
		return "SELECT * FROM payments WHERE registration_id = :registrationId";
	}
	
	@Override
	public String update()
	{
		return "UPDATE payments SET " +
				 "registration_id = :registrationId, " +
				 "authnet_transaction_id = :authnetTransactionId, " +
				 "cc_name_on_card = :ccNameOnCard, " +
				 "cc_expiration_month = :ccExpirationMonth, " +
				 "cc_expiration_year = :ccExpirationYear, " +
				 "cc_last_four_digits = :ccLastFourDigits, " +
				 "amount = :amount, " +
				 "transaction_timestamp = :transactionTimestamp " +
				 " WHERE  " +
				 "id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp) " + 
			   "VALUES (:id, :registrationId, :authnetTransactionId, :ccNameOnCard, :ccExpirationMonth, :ccExpirationYear, :ccLastFourDigits, :amount, :transactionTimestamp)";

	}

	@Override
	public String delete()
	{
		return "DELETE FROM payments WHERE id = :id";
	}

}
