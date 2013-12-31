package org.cru.crs.model;

import org.codehaus.jackson.JsonNode;
import org.cru.crs.domain.ProfileAttribute;

import java.util.List;
import java.util.UUID;

public class ProfileEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;

	private UUID userId;

	private JsonNode email;
	private JsonNode name;
	private JsonNode phone;
	private JsonNode address;
	private JsonNode birthDate;
	private JsonNode gender;
	private JsonNode campus;
	private JsonNode graduation;
	private JsonNode dormitory;

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

	public JsonNode getEmail()
	{
		return email;
	}

	public void setEmail(JsonNode email)
	{
		this.email = email;
	}

	public JsonNode getName()
	{
		return name;
	}

	public void setName(JsonNode name)
	{
		this.name = name;
	}

	public JsonNode getPhone()
	{
		return phone;
	}

	public void setPhone(JsonNode phone)
	{
		this.phone = phone;
	}

	public JsonNode getAddress()
	{
		return address;
	}

	public void setAddress(JsonNode address)
	{
		this.address = address;
	}

	public JsonNode getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(JsonNode birthDate)
	{
		this.birthDate = birthDate;
	}

	public JsonNode getGender()
	{
		return gender;
	}

	public void setGender(JsonNode gender)
	{
		this.gender = gender;
	}

	public JsonNode getCampus()
	{
		return campus;
	}

	public void setCampus(JsonNode campus)
	{
		this.campus = campus;
	}

	public JsonNode getGraduation()
	{
		return graduation;
	}

	public void setGraduation(JsonNode graduation)
	{
		this.graduation = graduation;
	}

	public JsonNode getDormitory()
	{
		return dormitory;
	}

	public void setDormitory(JsonNode dormitory)
	{
		this.dormitory = dormitory;
	}

	public void setProfileAttributes(List<ProfileAttribute> profileAttributes)
	{
		for (ProfileAttribute profileAttribute : profileAttributes)
		{
			switch (profileAttribute.getType())
			{
				case email:
					setEmail(profileAttribute.getValue());
					break;

				case name:
					setName(profileAttribute.getValue());
					break;

				case phone:
					setPhone(profileAttribute.getValue());
					break;

				case address:
					setAddress(profileAttribute.getValue());
					break;

				case birthDate:
					setBirthDate(profileAttribute.getValue());
					break;

				case gender:
					setGender(profileAttribute.getValue());
					break;

				case campus:
					setCampus(profileAttribute.getValue());
					break;

				case graduation:
					setGraduation(profileAttribute.getValue());
					break;

				case dormitory:
					setDormitory(profileAttribute.getValue());
					break;

				default:
			}
		}
	}
}
