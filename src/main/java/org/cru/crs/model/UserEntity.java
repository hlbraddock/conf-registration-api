package org.cru.crs.model;

import org.cru.crs.domain.ProfileAttribute;

import java.util.List;
import java.util.UUID;

public class UserEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;

	public UUID getId()
	{
		return id;
	}

	public UserEntity setId(UUID id)
	{
		this.id = id;
		return this;
	}

    public String getFirstName()
    {
        return firstName;
    }

    public UserEntity setFirstName(String firstName)
    {
        this.firstName = firstName;
        return this;
    }

    public String getLastName()
    {
        return lastName;
    }

    public UserEntity setLastName(String lastName)
    {
        this.lastName = lastName;
        return this;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public UserEntity setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
        return this;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public UserEntity setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
        return this;
    }

	public void setProfileAttributes(List<ProfileAttribute> profileAttributes)
	{
		for (ProfileAttribute profileAttribute : profileAttributes)
		{
			switch(profileAttribute.getType())
			{
				case firstName:
					setFirstName(profileAttribute.getValue());
					break;
				case lastName:
					setLastName(profileAttribute.getValue());
					break;
				case email:
					setEmailAddress(profileAttribute.getValue());
					break;
				case phone:
					setPhoneNumber(profileAttribute.getValue());
					break;
				default:
			}
		}
	}
}
