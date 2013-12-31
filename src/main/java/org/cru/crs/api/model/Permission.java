package org.cru.crs.api.model;

import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.cru.crs.jaxrs.JsonStandardDateTimeDeserializer;
import org.cru.crs.jaxrs.JsonStandardDateTimeSerializer;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.joda.time.DateTime;

public class Permission implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID conferenceId;
	private UUID userId;
	private String emailAddress;
	private String firstName;
	private String lastName;
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

	@JsonSerialize(using=JsonStandardDateTimeSerializer.class)
	public DateTime getTimestamp()
	{
		return timestamp;
	}

	@JsonDeserialize(using=JsonStandardDateTimeDeserializer.class)
	public void setTimestamp(DateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
