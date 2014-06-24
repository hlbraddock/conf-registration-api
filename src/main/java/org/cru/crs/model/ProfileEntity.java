package org.cru.crs.model;

import com.google.common.base.Strings;
import org.codehaus.jackson.JsonNode;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.answer.BlockType;
import org.cru.crs.api.model.answer.AddressQuestion;
import org.cru.crs.api.model.answer.DateQuestion;
import org.cru.crs.api.model.answer.NameQuestion;
import org.cru.crs.utils.JsonNodeHelper;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.UUID;

public class ProfileEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID userId;

	private DateTime birthDate;
	private String campus;
	private String city;
	private String dormitory;
	private String email;
	private String firstName;
	private String gender;
	private String yearInSchool;
	private String lastName;
	private String phone;
	private String state;
	private String address1;
	private String address2;
	private String zip;

	private DateTime createdTimestamp;
	private DateTime updatedTimestamp;

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

	public ProfileEntity(UUID id, UUID userId, DateTime birthDate, String campus, String city, String dormitory, String email, String firstName, String gender, String yearInSchool, String lastName, String phone, String state, String address1, String address2, String zip)
	{
		this(id, userId);

		this.birthDate = birthDate;
		this.campus = campus;
		this.city = city;
		this.dormitory = dormitory;
		this.email = email;
		this.firstName = firstName;
		this.gender = gender;
		this.yearInSchool = yearInSchool;
		this.lastName = lastName;
		this.phone = phone;
		this.state = state;
		this.address1 = address1;
		this.address2 = address2;
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

	public DateTime getBirthDate()
	{
		return birthDate;
	}

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

	public String getYearInSchool()
	{
		return yearInSchool;
	}

	public void setYearInSchool(String yearInSchool)
	{
		this.yearInSchool = yearInSchool;
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

	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	public DateTime getCreatedTimestamp()
	{
		return createdTimestamp;
	}

	public void setCreatedTimestamp(DateTime createdTimestamp)
	{
		this.createdTimestamp = createdTimestamp;
	}

	public DateTime getUpdatedTimestamp()
	{
		return updatedTimestamp;
	}

	public void setUpdatedTimestamp(DateTime updatedTimestamp)
	{
		this.updatedTimestamp = updatedTimestamp;
	}

	public void set(ProfileEntity profileEntity)
	{
		if (profileEntity == null)
			return;

		if (profileEntity.getBirthDate() != null)
			birthDate = profileEntity.getBirthDate();

		if (!Strings.isNullOrEmpty(profileEntity.getCampus()))
			campus = profileEntity.getCampus();

		if (!Strings.isNullOrEmpty(profileEntity.getCity()))
			city = profileEntity.getCity();

		if (!Strings.isNullOrEmpty(profileEntity.getDormitory()))
			dormitory = profileEntity.getDormitory();

		if (!Strings.isNullOrEmpty(profileEntity.getEmail()))
			email = profileEntity.getEmail();

		if (!Strings.isNullOrEmpty(profileEntity.getFirstName()))
			firstName = profileEntity.getFirstName();

		if (!Strings.isNullOrEmpty(profileEntity.getGender()))
			gender = profileEntity.getGender();

		if (profileEntity.getYearInSchool() != null)
			yearInSchool = profileEntity.getYearInSchool();

		if (!Strings.isNullOrEmpty(profileEntity.getLastName()))
			lastName = profileEntity.getLastName();

		if (!Strings.isNullOrEmpty(profileEntity.getPhone()))
			phone = profileEntity.getPhone();

		if (!Strings.isNullOrEmpty(profileEntity.getState()))
			state = profileEntity.getState();

		if (!Strings.isNullOrEmpty(profileEntity.getAddress1()))
			address1 = profileEntity.getAddress1();

		if (!Strings.isNullOrEmpty(profileEntity.getAddress2()))
			address2 = profileEntity.getAddress2();

		if (!Strings.isNullOrEmpty(profileEntity.getZip()))
			zip = profileEntity.getZip();

		createdTimestamp = profileEntity.getCreatedTimestamp();
		updatedTimestamp = profileEntity.getUpdatedTimestamp();
	}

	private boolean hasProfileType(BlockEntity blockEntity)
	{
		return blockEntity.getProfileType() != null && !Strings.isNullOrEmpty(blockEntity.getProfileType().toString());
	}

	Logger logger = Logger.getLogger(ProfileEntity.class);

	// set profile from answers
	public void set(Map<Answer, BlockEntity> answerToBlockMap)
	{
		for (Map.Entry<Answer, BlockEntity> entry : answerToBlockMap.entrySet())
		{
			Answer answer = entry.getKey();

			BlockEntity blockEntity = entry.getValue();

			if (hasProfileType(blockEntity))
			{
				try
				{
					BlockType blockType = BlockType.fromString(blockEntity.getBlockType());

					if (blockType.isJsonFormat())
					{
						if (blockType.equals(BlockType.NAME_QUESTION))
						{
							NameQuestion nameQuestion = JsonNodeHelper.deserialize(answer.getValue(), NameQuestion.class);

							firstName = nameQuestion.getFirstName();
							lastName = nameQuestion.getLastName();
						}
						else if (blockType.equals(BlockType.ADDRESS_QUESTION))
						{
							AddressQuestion addressQuestion = JsonNodeHelper.deserialize(answer.getValue(), AddressQuestion.class);

							address1 = addressQuestion.getAddress1();
							address2 = addressQuestion.getAddress2();
							city = addressQuestion.getCity();
							state = addressQuestion.getState();
							zip = addressQuestion.getZip();
						}
					}
					else if (blockType.isTextFormat())
					{
						if (blockType.equals(BlockType.DATE_QUESTION))
						{
							JsonNode jsonNode = JsonNodeHelper.toJsonNode(JsonNodeHelper.toJsonString(answer.getValue()));
							DateQuestion dateQuestion = JsonNodeHelper.deserialize(jsonNode, DateQuestion.class);

							if (blockEntity.getProfileType().equals(ProfileType.BIRTH_DATE))
							{
								birthDate = dateQuestion.getText();
							}
						}
						else
						{
							switch (blockEntity.getProfileType())
							{
								case CAMPUS:
									campus = answer.getValue().getTextValue();
									break;
								case DORMITORY:
									dormitory = answer.getValue().getTextValue();
									break;
								case EMAIL:
									email = answer.getValue().getTextValue();
									break;
								case GENDER:
									gender = answer.getValue().getTextValue();
									break;
								case PHONE:
									phone = answer.getValue().getTextValue();
									break;
								case YEAR_IN_SCHOOL:
									yearInSchool = answer.getValue().getTextValue();
									break;
							}
						}
					}
				}
				catch (Exception e)
				{
					logger.error("Could not set profile from answers for block type " + blockEntity.getBlockType() +
							" and profile type " + blockEntity.getProfileType(), e);
				}
			}
		}
	}

	public static ProfileEntity from(Map<Answer, BlockEntity> answerToBlockMap)
	{
		ProfileEntity profileEntity = new ProfileEntity();

		profileEntity.set(answerToBlockMap);

		return profileEntity;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProfileEntity that = (ProfileEntity) o;

		if (address1 != null ? !address1.equals(that.address1) : that.address1 != null) return false;
		if (address2 != null ? !address2.equals(that.address2) : that.address2 != null) return false;
		if (birthDate != null ? !birthDate.equals(that.birthDate) : that.birthDate != null) return false;
		if (campus != null ? !campus.equals(that.campus) : that.campus != null) return false;
		if (city != null ? !city.equals(that.city) : that.city != null) return false;
		if (dormitory != null ? !dormitory.equals(that.dormitory) : that.dormitory != null) return false;
		if (email != null ? !email.equals(that.email) : that.email != null) return false;
		if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
		if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
		if (yearInSchool != null ? !yearInSchool.equals(that.yearInSchool) : that.yearInSchool != null) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
		if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
		if (state != null ? !state.equals(that.state) : that.state != null) return false;
		if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
		if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
		result = 31 * result + (campus != null ? campus.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (dormitory != null ? dormitory.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (gender != null ? gender.hashCode() : 0);
		result = 31 * result + (yearInSchool != null ? yearInSchool.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (phone != null ? phone.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (address1 != null ? address1.hashCode() : 0);
		result = 31 * result + (address2 != null ? address2.hashCode() : 0);
		result = 31 * result + (zip != null ? zip.hashCode() : 0);
		return result;
	}
}

