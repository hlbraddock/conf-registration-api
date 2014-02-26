package org.cru.crs.model;

import java.util.UUID;

public class UserEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;

	// constructors

	public UserEntity()
	{
	}

	public UserEntity(String firstName, String lastName, String emailAddress, String phoneNumber)
	{
		this(null, firstName, lastName, emailAddress, phoneNumber);
	}

	public UserEntity(UUID id, String firstName, String lastName, String emailAddress, String phoneNumber)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNumber = phoneNumber;
	}

	// getter/setters

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

	public static UserEntity from(ProfileEntity profileEntity)
	{
		return new UserEntity(UUID.randomUUID(), profileEntity.getFirstName(), profileEntity.getLastName(), profileEntity.getEmail(), profileEntity.getPhone());
	}
}
