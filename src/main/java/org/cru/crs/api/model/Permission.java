package org.cru.crs.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.joda.time.DateTime;

import java.util.UUID;

public class Permission implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final Integer MONTHS_BEFORE_UNACCEPTED_PERMISSION_EXPIRES = 2;
	
	private UUID id;
	private UUID conferenceId;
	private UUID userId;
	private String emailAddress;
	private UUID givenByUserId;
	private PermissionLevel permissionLevel;
	private DateTime timestamp;
	
	public static Permission fromDb(PermissionEntity dbPermission)
	{
		Permission webPermission = new Permission();
		
		webPermission.id = dbPermission.getId();
		webPermission.conferenceId = dbPermission.getConferenceId();
		webPermission.userId = dbPermission.getUserId();
		webPermission.givenByUserId = dbPermission.getGivenByUserId();
		webPermission.timestamp = dbPermission.getLastUpdatedTimestamp();
		webPermission.permissionLevel = dbPermission.getPermissionLevel();
		webPermission.emailAddress = dbPermission.getEmailAddress();
		
		return webPermission;
	}
	
	public PermissionEntity toDbPermissionEntity()
	{
		PermissionEntity dbPermissionEntity = new PermissionEntity();
		
		dbPermissionEntity.setId(id);
		dbPermissionEntity.setUserId(userId);
		dbPermissionEntity.setConferenceId(conferenceId);
		dbPermissionEntity.setGivenByUserId(givenByUserId);
		dbPermissionEntity.setPermissionLevel(permissionLevel);
		dbPermissionEntity.setLastUpdatedTimestamp(timestamp);
		dbPermissionEntity.setEmailAddress(emailAddress);
		
		return dbPermissionEntity;
	}
	
	public int hashCode()
	{
		return new HashCodeBuilder(17, 29) // two randomly chosen prime numbers
					.append(id)
					.append(permissionLevel)
					.toHashCode();
	}

	public boolean equals(Permission obj)
	{
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Permission))
			return false;

		Permission rhs = (Permission) obj;
		return new EqualsBuilder()
				.append(id, rhs.id)
				.append(permissionLevel, rhs.permissionLevel)
				.isEquals();
	}

	public Permission withRandomID()
	{
		this.id = UUID.randomUUID();
		return this;
	}
	
	public UUID getId()
	{
		return id;
	}

	public Permission setId(UUID id)
	{
		this.id = id;
		return this;
	}

	public UUID getConferenceId()
	{
		return conferenceId;
	}

	public Permission setConferenceId(UUID conferenceId)
	{
		this.conferenceId = conferenceId;
		return this;
	}

	public UUID getUserId()
	{
		return userId;
	}

	public Permission setUserId(UUID userId)
	{
		this.userId = userId;
		return this;
	}

	public UUID getGivenByUserId()
	{
		return givenByUserId;
	}

	public Permission setGivenByUserId(UUID givenByUserId)
	{
		this.givenByUserId = givenByUserId;
		return this;
	}

	public PermissionLevel getPermissionLevel()
	{
		return permissionLevel;
	}

	public Permission setPermissionLevel(PermissionLevel permissionLevel)
	{
		this.permissionLevel = permissionLevel;
		return this;
	}

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getTimestamp()
	{
		return timestamp;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public Permission setTimestamp(DateTime timestamp)
	{
		this.timestamp = timestamp;
		return this;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public Permission setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
		return this;
	}
}
