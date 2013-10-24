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
		StringBuilder query = new StringBuilder();
		query.append("UPDATE payments SET ")
				.append("registration_id = :registrationId,")
				.append("authnet_transaction_id = :authnetTransactionId,")
				.append("cc_name_on_card = :ccNameOnCard,")
				.append("cc_expiration_month = :ccExpirationMonth,")
				.append("cc_expiration_year = :ccExpirationYear,")
				.append("cc_last_four_digits = :ccLastFourDigits,")
				.append("amount = :amount,")
				.append("transaction_datetime = transactionDatetime")
				.append(" WHERE ")
				.append("id = :id");
						
				
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO payments(")
				.append("id,")
				.append("registration_id")
				.append("authnet_transaction_id")
				.append("cc_name_on_card")
				.append("cc_expiration_month")
				.append("cc_expiration_year")
				.append("cc_last_four_digits")
				.append("amount")
				.append("transaction_datetime")
				.append(") VALUES (")
				.append(":id")
				.append(":registrationId")
				.append(":authnetTransactionId")
				.append(":ccNameOnCard")
				.append(":ccExpirationMonth")
				.append(":ccExpirationYear")
				.append(":ccLastFourDigits")
				.append(":amount")
				.append(":transactionDatetime")
				.append(")");
						
		return query.toString();

	}

	@Override
	public String delete()
	{
		return "DELETE FROM payments WHERE id = :id";
	}

}
