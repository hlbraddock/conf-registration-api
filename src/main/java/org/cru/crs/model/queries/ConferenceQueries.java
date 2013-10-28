package org.cru.crs.model.queries;

import org.ccci.util.NotImplementedException;

public class ConferenceQueries implements BasicQueries
{
	
	public String selectById()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM conferences WHERE id = :id");
		
		return query.toString();
	}
	
	public String selectAllForUser()
	{
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM conferences WHERE contact_person_id = :contactPersonId");
		
		return query.toString();
	}
	
	public String insert()
	{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO conferences (")
			 .append("id,")
			 .append("name,")
			 .append("description,")
			 .append("event_start_time,")
			 .append("event_end_time,")
			 .append("registration_start_time,")
			 .append("registration_end_time,")
			 .append("total_slots,")
			 .append("contact_person_id,")
			 .append("contact_person_name,")
			 .append("contact_person_email,")
			 .append("contact_person_phone,")
			 .append("conference_costs_id,")
			 .append("location_name,")
			 .append("location_address,")
			 .append("location_city,")
			 .append("location_state,")
			 .append("location_zip_code")
			 .append(") VALUES(")
			 .append(":id,")
			 .append(":name,")
			 .append(":description,")
			 .append(":eventStartTime,")
			 .append(":eventEndTime,")
			 .append(":registrationStartTime,")
			 .append(":registrationEndTime,")
			 .append(":totalSlots,")
			 .append(":contactPersonId,")
			 .append(":contactPersonName,")
			 .append(":contactPersonEmail,")
			 .append(":contactPersonPhone,")
			 .append(":conferenceCostsId,")
			 .append(":locationName,")
			 .append(":locationAddress,")
			 .append(":locationCity,")
			 .append(":locationState,")
			 .append(":locationZipCode")
			 .append(")");
		
		return query.toString();
	}
	
	public String update()
	{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE conferences SET ")
			 .append("name = :name,")
			 .append("description = :description,")
			 .append("event_start_time = :eventStartTime,")
			 .append("event_end_time = :eventEndTime,")
			 .append("registration_start_time = :registrationStartTime,")
			 .append("registration_end_time = :registrationEndTime,")
			 .append("total_slots = :totalSlots,")
			 .append("contact_person_id = :contactPersonId,")
			 .append("contact_person_name = :contactPersonName,")
			 .append("contact_person_email = :contactPersonEmail,")
			 .append("contact_person_phone = :contactPersonPhone,")
			 .append("conference_costs_id = :conferenceCostsId,")
			 .append("location_name = :locationName,")
			 .append("location_address = :locationAddress,")
			 .append("location_city = :locationCity,")
			 .append("location_state = :locationState,")
			 .append("location_zip_code = :locationZipCode")
			 .append(" WHERE ")
			 .append("id = :id");

		return query.toString();
	}
	
	public String delete()
	{
		throw new NotImplementedException();
	}
}
