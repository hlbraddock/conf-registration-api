package org.cru.crs.model;

import com.google.common.base.Strings;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.answer.AddressQuestion;
import org.cru.crs.api.model.answer.BlockType;
import org.cru.crs.api.model.answer.DateQuestion;
import org.cru.crs.api.model.answer.NameQuestion;
import org.cru.crs.api.model.answer.TextQuestion;
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
	private DateTime graduation;
	private String lastName;
	private String phone;
	private String state;
	private String address1;
	private String address2;
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

	public ProfileEntity(UUID id, UUID userId, DateTime birthDate, String campus, String city, String dormitory, String email, String firstName, String gender, DateTime graduation, String lastName, String phone, String state, String address1, String address2, String zip)
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

	public DateTime getGraduation()
	{
		return graduation;
	}

	public void setGraduation(DateTime graduation)
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

	public TextQuestion getTextQuestion(ProfileType profileType)
	{
		TextQuestion textQuestion = new TextQuestion();

		switch(profileType)
		{
			case EMAIL:
				textQuestion.setText(email);
				break;
			case PHONE:
				textQuestion.setText(phone);
				break;
			case GENDER:
				textQuestion.setText(gender);
				break;
			case CAMPUS:
				textQuestion.setText(campus);
				break;
			case DORMITORY:
				textQuestion.setText(dormitory);
				break;
			default:
		}

		return textQuestion;
	}

	public DateQuestion getDateQuestion(ProfileType profileType)
	{
		DateQuestion dateQuestion = new DateQuestion();

		switch(profileType)
		{
			case BIRTH_DATE:
				dateQuestion.setText(birthDate);
				break;
			case GRADUATION:
				dateQuestion.setText(graduation);
			default:
		}

		return dateQuestion;
	}

	public NameQuestion getNameQuestion()
	{
		NameQuestion nameQuestion = new NameQuestion();

		nameQuestion.setFirstName(firstName);
		nameQuestion.setLastName(lastName);

		return nameQuestion;
	}

	public AddressQuestion getAddressQuestion()
	{
		AddressQuestion addressQuestion = new AddressQuestion();

		addressQuestion.setAddress1(address1);
		addressQuestion.setAddress2(address2);
		addressQuestion.setCity(city);
		addressQuestion.setState(state);
		addressQuestion.setZip(zip);

		return addressQuestion;
	}

	public void set(TextQuestion textQuestion, ProfileType profileType)
	{
		switch(profileType)
		{
			case EMAIL:
				setEmail(textQuestion.getText());
				break;
			case PHONE:
				setPhone(textQuestion.getText());
				break;
			case GENDER:
				setGender(textQuestion.getText());
				break;
			case CAMPUS:
				setCampus(textQuestion.getText());
				break;
			case DORMITORY:
				setDormitory(textQuestion.getText());
				break;
			default:
		}
	}

	public void set(DateQuestion dateQuestion, ProfileType profileType)
	{
		switch(profileType)
		{
			case BIRTH_DATE:
				setBirthDate(dateQuestion.getText());
				break;
			case GRADUATION:
				setGraduation(dateQuestion.getText());
				break;
			default:
		}
	}

	public void set(NameQuestion nameQuestion)
	{
		firstName = nameQuestion.getFirstName();
		lastName = nameQuestion.getLastName();
	}

	public void set(AddressQuestion addressQuestion)
	{
		address1 = addressQuestion.getAddress1();
		address2 = addressQuestion.getAddress2();
		city = addressQuestion.getCity();
		state = addressQuestion.getState();
		zip = addressQuestion.getZip();
	}

	public void set(ProfileEntity profileEntity)
	{
		if(profileEntity == null)
			return;

		if(profileEntity.getBirthDate() != null)
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

		if(profileEntity.getGraduation() != null)
			graduation = profileEntity.getGraduation();

		if(!Strings.isNullOrEmpty(profileEntity.getLastName()))
			lastName = profileEntity.getLastName();

		if(!Strings.isNullOrEmpty(profileEntity.getPhone()))
			phone = profileEntity.getPhone();

		if(!Strings.isNullOrEmpty(profileEntity.getState()))
			state = profileEntity.getState();

		if(!Strings.isNullOrEmpty(profileEntity.getAddress1()))
			address1 = profileEntity.getAddress1();

		if(!Strings.isNullOrEmpty(profileEntity.getAddress2()))
			address2 = profileEntity.getAddress2();

		if(!Strings.isNullOrEmpty(profileEntity.getZip()))
			zip = profileEntity.getZip();
	}

	private boolean hasProfileType(BlockEntity blockEntity)
	{
		return blockEntity.getProfileType() != null && !Strings.isNullOrEmpty(blockEntity.getProfileType().toString());
	}

	Logger logger = Logger.getLogger(ProfileEntity.class);

	// set profile from answers
	public void set(Map<Answer,BlockEntity> answerToBlockMap)
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

					// deserialize the json answer using the appropriate block type determined answer object
					if(blockType.isTextQuestion())
					{
						TextQuestion textQuestion = JsonNodeHelper.deserialize(answer.getValue(), TextQuestion.class);
						set(textQuestion, blockEntity.getProfileType());
					}

					else if(blockType.isDateQuestion())
					{
						DateQuestion dateQuestion = JsonNodeHelper.deserialize(answer.getValue(), DateQuestion.class);
						set(dateQuestion, blockEntity.getProfileType());
					}

					else if(blockType.isNameQuestion())
					{
						NameQuestion nameQuestion = JsonNodeHelper.deserialize(answer.getValue(), NameQuestion.class);
						set(nameQuestion);
					}

					else if(blockType.isAddressQuestion())
					{
						AddressQuestion addressQuestion = JsonNodeHelper.deserialize(answer.getValue(), AddressQuestion.class);
						set(addressQuestion);
					}
				}
				catch(Exception e)
				{
					logger.error("Could not set profile from answers for block type " + blockEntity.getBlockType() +
							" and profile type " + blockEntity.getProfileType(), e);
				}
			}
		}
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
		if (graduation != null ? !graduation.equals(that.graduation) : that.graduation != null) return false;
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
		result = 31 * result + (graduation != null ? graduation.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (phone != null ? phone.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (address1 != null ? address1.hashCode() : 0);
		result = 31 * result + (address2 != null ? address2.hashCode() : 0);
		result = 31 * result + (zip != null ? zip.hashCode() : 0);
		return result;
	}
}

