package org.cru.crs.model.queries;

public class UserQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM users WHERE id = :id";
	}

	@Override
	public String update()
	{
		return "UPDATE users SET " +
				 "first_name = :firstName, " +
				 "last_name = :lastName, " +
				 "phone_number = :phoneNumber, " +
				 "email_address = :emailAddress " +
				 " WHERE " +
				 "id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO users(id, first_name, last_name, phone_number, email_address) VALUES (:id, :firstName, :lastName, :phoneNumber, :emailAddress)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM users WHERE id = :id";
	}
	
}
