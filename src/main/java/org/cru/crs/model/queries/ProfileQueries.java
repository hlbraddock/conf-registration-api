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
				"birthDate = :birthDate, " +
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
		return "INSERT INTO profiles(id, email, name, phone, address, birthDate, gender, campus, graduation, dormitory) VALUES " +
				"(:id, :email, :name, :phone, :address, :birthDate, :gender, :campus, :graduation, :dormitory)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM profiles WHERE id = :id";
	}
	
}
