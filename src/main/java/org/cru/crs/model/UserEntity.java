package org.cru.crs.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "USERS")
public class UserEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Type(type="pg-uuid")
	private UUID id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "CONTACT_EMAIL")
    private String emailAddress;

    @Column(name = "PHONE_NUMBER")
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
}
