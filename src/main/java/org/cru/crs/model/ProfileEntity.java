package org.cru.crs.model;

import com.google.common.base.Strings;

import java.util.UUID;

public class ProfileEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;

	private UUID userId;

	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String birthDate;
	private String gender;
	private String campus;
	private String graduation;
	private String dormitory;

	public ProfileEntity()
	{
		id = UUID.randomUUID();
	}

	public ProfileEntity(UUID userId)
	{
		this();
		this.userId = userId;
	}

	public UUID getId()
	{
		return id;
	}

	public ProfileEntity setId(UUID id)
	{
		this.id = id;
		return this;
	}

	public UUID getUserId()
	{
		return userId;
	}

	public void setUserId(UUID userId)
	{
		this.userId = userId;
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

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	public String getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(String birthDate)
	{
		this.birthDate = birthDate;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getCampus()
	{
		return campus;
	}

	public void setCampus(String campus)
	{
		this.campus = campus;
	}

	public String getGraduation()
	{
		return graduation;
	}

	public void setGraduation(String graduation)
	{
		this.graduation = graduation;
	}

	public String getDormitory()
	{
		return dormitory;
	}

	public void setDormitory(String dormitory)
	{
		this.dormitory = dormitory;
	}
}

