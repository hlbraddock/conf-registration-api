package org.cru.crs.model;


import java.util.UUID;

public class RegistrationEntity implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID conferenceId;
	private UUID userId;
    private Boolean completed;
    
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getConferenceId() {
		return conferenceId;
	}
	public void setConferenceId(UUID conferenceId) {
		this.conferenceId = conferenceId;
	}
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

}
