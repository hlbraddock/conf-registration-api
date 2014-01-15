package org.cru.crs.model;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public class PermissionEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private UUID conferenceId;
    private UUID userId;
    private String emailAddress;
    private UUID givenByUserId;
    private PermissionLevel permissionLevel;
    private String activationCode;
    private DateTime lastUpdatedTimestamp;
    
	public int hashCode()
	{
		return new HashCodeBuilder(29, 83). // two randomly chosen prime numbers
				append(id).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof PermissionEntity)) return false;

		PermissionEntity rhs = (PermissionEntity) obj;
		return new EqualsBuilder().
				append(id, rhs.id).
				isEquals();
	}
    
	public UUID getId()
	{
		return id;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	
	public UUID getConferenceId()
	{
		return conferenceId;
	}
	
	public void setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
	}
	
	public UUID getUserId()
	{
		return userId;
	}
	
	public void setUserId(UUID userId)
	{
		this.userId = userId;
	}
	
	public UUID getGivenByUserId()
	{
		return givenByUserId;
	}
	
	public void setGivenByUserId(UUID givenByUserId)
	{
		this.givenByUserId = givenByUserId;
	}
	
	public PermissionLevel getPermissionLevel()
	{
		return permissionLevel;
	}
	
	public void setPermissionLevel(PermissionLevel permissionLevel)
	{
		this.permissionLevel = permissionLevel;
	}
	
	public DateTime getLastUpdatedTimestamp()
	{
		return lastUpdatedTimestamp;
	}
	
	public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp)
	{
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}

	public String getActivationCode()
	{
		return activationCode;
	}

	public void setActivationCode(String activationCode)
	{
		this.activationCode = activationCode;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}
}
