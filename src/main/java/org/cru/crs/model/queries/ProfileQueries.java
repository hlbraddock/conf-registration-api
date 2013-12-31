package org.cru.crs.model.queries;

public class ProfileQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM profiles WHERE :id = id";
	}

	public String selectByUserId()
	{
		return "SELECT * FROM profiles WHERE :id = user_id";
	}

	@Override
	public String update()
	{
		return "UPDATE profiles SET " +
				"email = :email, " +
				"name = :name, " +
				"phone = :phone, " +
				"address = :address, " +
				"birth_date = :birthDate, " +
				"gender = :gender, " +
				"campus = :campus, " +
				"graduation = :graduation, " +
				"dormitory = :dormitory " +
				 "WHERE " +
				 "id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO profiles(id, user_id, email, name, phone, address, birth_date, gender, campus, graduation, dormitory) VALUES " +
				"(:id, :userId, :email, :name, :phone, :address, :birthDate, :gender, :campus, :graduation, :dormitory)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM profiles WHERE id = :id";
	}
}
