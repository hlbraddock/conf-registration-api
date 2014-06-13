package org.cru.crs.model.queries;

public class ProfileQueries implements BasicQueries
{

	@Override
	public String selectById()
	{
		return "SELECT * FROM profiles WHERE id = :id";
	}

	public String selectByUserId()
	{
		return "SELECT * FROM profiles WHERE user_id = :userId";
	}

	@Override
	public String update()
	{
		return "UPDATE profiles SET " +
				"email = :email, " +
				"first_name = :firstName, " +
				"last_name = :lastName, " +
				"phone = :phone, " +
				"address1 = :address1, " +
				"address2 = :address2, " +
				"city = :city, " +
				"state = :state, " +
				"zip = :zip, " +
				"birth_date = :birthDate, " +
				"gender = :gender, " +
				"campus = :campus, " +
				"year_in_school = :yearInSchool, " +
				"dormitory = :dormitory " +
				 "WHERE " +
				 "id = :id";
	}

	@Override
	public String insert()
	{
		return "INSERT INTO profiles(id, user_id, email, first_name, last_name, phone, address1, address2, city, state, zip, birth_date, gender, campus, year_in_school, dormitory) VALUES " +
				"(:id, :userId, :email, :firstName, :lastName, :phone, :address1, :address2, :city, :state, :zip, :birthDate, :gender, :campus, :yearInSchool, :dormitory)";
	}

	@Override
	public String delete()
	{
		return "DELETE FROM profiles WHERE id = :id";
	}
}
