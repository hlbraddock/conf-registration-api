package org.cru.crs.model.queries;

public class ConferenceQueries implements BasicQueries
{
	
	public String selectById()
	{
		return "SELECT * FROM conferences WHERE id = :id";
	}
	
	public String insert()
	{
		return "INSERT INTO conferences (id, name, description, event_start_time, event_end_time, registration_start_time, registration_end_time, total_slots," +
			 	"contact_person_name, contact_person_email, contact_person_phone, conference_costs_id, location_name, location_address, location_city, " +
			 	"location_state, location_zip_code, require_login, archived) " +
			 	"VALUES(:id, :name, :description, :eventStartTime, :eventEndTime, :registrationStartTime, :registrationEndTime, :totalSlots," +
			 	":contactPersonName, :contactPersonEmail, :contactPersonPhone, :conferenceCostsId, :locationName, :locationAddress, :locationCity, " +
			 	":locationState, :locationZipCode, :requireLogin, :archived)";
		
	}
	
	public String update()
	{
		return "UPDATE conferences SET  " +
			  "name = :name, " +
			  "description = :description, " +
			  "event_start_time = :eventStartTime, " +
			  "event_end_time = :eventEndTime, " +
			  "registration_start_time = :registrationStartTime, " +
			  "registration_end_time = :registrationEndTime, " +
			  "total_slots = :totalSlots, " +
			  "contact_person_name = :contactPersonName, " +
			  "contact_person_email = :contactPersonEmail, " +
			  "contact_person_phone = :contactPersonPhone, " +
			  "conference_costs_id = :conferenceCostsId, " +
			  "location_name = :locationName, " +
			  "location_address = :locationAddress, " +
			  "location_city = :locationCity, " +
			  "location_state = :locationState, " +
			  "location_zip_code = :locationZipCode, " +
			  "require_login = :requireLogin, " +
              "archived = :archived " +
			  " WHERE " +
			  "id = :id ";
	}
	
	public String delete()
	{
		return "DELETE FROM conferences WHERE " +
               "id = :id ";
	}
}
