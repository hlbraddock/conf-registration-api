package org.cru.crs.model;

import com.google.common.base.Strings;

import java.util.UUID;

public class ProfileEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID userId;

	private String birthDate;
	private String campus;
	private String city;
	private String dormitory;
	private String email;
	private String firstName;
	private String gender;
	private String graduation;
	private String lastName;
	private String phone;
	private String state;
	private String street;
	private String zip;

	public ProfileEntity()
	{
	}

	public ProfileEntity(UUID id)
	{
		this();
		this.id = id;
	}

	public ProfileEntity(UUID id, UUID userId)
	{
		this(id);
		this.userId = userId;
	}

	public ProfileEntity(UUID id, UUID userId, String birthDate, String campus, String city, String dormitory, String email, String firstName, String gender, String graduation, String lastName, String phone, String state, String street, String zip)
	{
		this(id, userId);

		this.birthDate = birthDate;
		this.campus = campus;
		this.city = city;
		this.dormitory = dormitory;
		this.email = email;
		this.firstName = firstName;
		this.gender = gender;
		this.graduation = graduation;
		this.lastName = lastName;
		this.phone = phone;
		this.state = state;
		this.street = street;
		this.zip = zip;
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

	public String getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(String birthDate)
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

	public String getGraduation()
	{
		return graduation;
	}

	public void setGraduation(String graduation)
	{
		this.graduation = graduation;
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

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	public void set(ProfileEntity profileEntity)
	{
		if(profileEntity == null)
			return;

		if(!Strings.isNullOrEmpty(profileEntity.getBirthDate()))
			birthDate = profileEntity.getBirthDate();

		if(!Strings.isNullOrEmpty(profileEntity.getCampus()))
			campus = profileEntity.getCampus();

		if(!Strings.isNullOrEmpty(profileEntity.getCity()))
			city = profileEntity.getCity();

		if(!Strings.isNullOrEmpty(profileEntity.getDormitory()))
			dormitory = profileEntity.getDormitory();

		if(!Strings.isNullOrEmpty(profileEntity.getEmail()	))
			email = profileEntity.getEmail();

		if(!Strings.isNullOrEmpty(profileEntity.getFirstName()))
			firstName = profileEntity.getFirstName();

		if(!Strings.isNullOrEmpty(profileEntity.getGender()))
			gender = profileEntity.getGender();

		if(!Strings.isNullOrEmpty(profileEntity.getGraduation()))
			graduation = profileEntity.getGraduation();

		if(!Strings.isNullOrEmpty(profileEntity.getLastName()))
			lastName = profileEntity.getLastName();

		if(!Strings.isNullOrEmpty(profileEntity.getPhone()))
			phone = profileEntity.getPhone();

		if(!Strings.isNullOrEmpty(profileEntity.getState()))
			state = profileEntity.getState();

		if(!Strings.isNullOrEmpty(profileEntity.getStreet()))
			street = profileEntity.getStreet();

		if(!Strings.isNullOrEmpty(profileEntity.getZip()))
			zip = profileEntity.getZip();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProfileEntity that = (ProfileEntity) o;

		if (birthDate != null ? !birthDate.equals(that.birthDate) : that.birthDate != null) return false;
		if (campus != null ? !campus.equals(that.campus) : that.campus != null) return false;
		if (city != null ? !city.equals(that.city) : that.city != null) return false;
		if (dormitory != null ? !dormitory.equals(that.dormitory) : that.dormitory != null) return false;
		if (email != null ? !email.equals(that.email) : that.email != null) return false;
		if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
		if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
		if (graduation != null ? !graduation.equals(that.graduation) : that.graduation != null) return false;
		if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
		if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
		if (state != null ? !state.equals(that.state) : that.state != null) return false;
		if (street != null ? !street.equals(that.street) : that.street != null) return false;
		if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
		if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = userId != null ? userId.hashCode() : 0;
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (phone != null ? phone.hashCode() : 0);
		result = 31 * result + (street != null ? street.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (zip != null ? zip.hashCode() : 0);
		result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
		result = 31 * result + (gender != null ? gender.hashCode() : 0);
		result = 31 * result + (campus != null ? campus.hashCode() : 0);
		result = 31 * result + (graduation != null ? graduation.hashCode() : 0);
		result = 31 * result + (dormitory != null ? dormitory.hashCode() : 0);
		return result;
	}
}

