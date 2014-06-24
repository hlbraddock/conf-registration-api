package org.cru.crs.api.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.ProfileEntity;
import org.joda.time.DateTime;

import java.util.UUID;

public class Profile implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID userId;

	private String address1;
	private String address2;
	private DateTime birthDate;
	private String campus;
	private String city;
	private String dormitory;
	private String email;
	private String firstName;
	private String gender;
	private String lastName;
	private String phone;
	private String state;
	private String yearInSchool;
	private String zip;

	public Profile()
	{
	}

	public Profile(UUID id, UUID userId, String address1, String address2, DateTime birthDate, String campus, String city, String dormitory, String email, String firstName, String gender, String lastName, String phone, String state, String yearInSchool, String zip)
	{
		this.id = id;
		this.userId = userId;
		this.address1 = address1;
		this.address2 = address2;
		this.birthDate = birthDate;
		this.campus = campus;
		this.city = city;
		this.dormitory = dormitory;
		this.email = email;
		this.firstName = firstName;
		this.gender = gender;
		this.lastName = lastName;
		this.phone = phone;
		this.state = state;
		this.yearInSchool = yearInSchool;
		this.zip = zip;
	}

	public static Profile fromDb(ProfileEntity dbProfile)
	{
		return new Profile(
				dbProfile.getId(),
				dbProfile.getUserId(),
				dbProfile.getAddress1(),
				dbProfile.getAddress2(),
				dbProfile.getBirthDate(),
				dbProfile.getCampus(),
				dbProfile.getCity(),
				dbProfile.getDormitory(),
				dbProfile.getEmail(),
				dbProfile.getFirstName(),
				dbProfile.getGender(),
				dbProfile.getLastName(),
				dbProfile.getPhone(),
				dbProfile.getState(),
				dbProfile.getYearInSchool(),
				dbProfile.getZip());
	}

	public ProfileEntity toDbProfileEntity()
	{
		ProfileEntity jpaProfile = new ProfileEntity();

		jpaProfile.setId(id);
		jpaProfile.setUserId(userId);
		jpaProfile.setAddress1(address1);
		jpaProfile.setAddress2(address2);
		jpaProfile.setBirthDate(birthDate);
		jpaProfile.setCampus(campus);
		jpaProfile.setCity(city);
		jpaProfile.setDormitory(dormitory);
		jpaProfile.setEmail(email);
		jpaProfile.setFirstName(firstName);
		jpaProfile.setGender(gender);
		jpaProfile.setLastName(lastName);
		jpaProfile.setPhone(phone);
		jpaProfile.setState(state);
		jpaProfile.setYearInSchool(yearInSchool);
		jpaProfile.setZip(zip);

		return jpaProfile;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getUserId()
	{
		return userId;
	}

	public void setUserId(UUID userId)
	{
		this.userId = userId;
	}

	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getBirthDate()
	{
		return birthDate;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setBirthDate(DateTime birthDate)
	{
		this.birthDate = birthDate;
	}

	public String getCampus()
	{
		return campus;
	}

	public void setCampus(String campus)
	{
		this.campus = campus;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getDormitory()
	{
		return dormitory;
	}

	public void setDormitory(String dormitory)
	{
		this.dormitory = dormitory;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getYearInSchool()
	{
		return yearInSchool;
	}

	public void setYearInSchool(String yearInSchool)
	{
		this.yearInSchool = yearInSchool;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}
}
