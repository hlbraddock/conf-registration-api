package org.cru.crs.model.queries;

public class UserQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM users WHERE :id = id";
	}

	@Override
	public String update()
	{
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE users SET ")
				.append("first_name = :firstName")
				.append("last_name = :lastName")
				.append("phone_number = phoneNumber")
				.append("email_address = emailAddress")
				.append(" WHERE ")
				.append("id = :id");
		
		return query.toString();
	}

	@Override
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO users( ")
				.append("id,")
				.append("first_name,")
				.append("last_name,")
				.append("phone_number,")
				.append("email_address,")
				.append(") VALUES ( ")
				.append(":id,")
				.append(":firstName")
				.append(":lastName")
				.append(":phoneNumber")
				.append(":emailAddress");
		
		return query.toString();
	}

	@Override
	public String delete()
	{
		return "DELETE FROM users WHERE id = :id";
	}
	
}
