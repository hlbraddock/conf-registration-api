package org.cru.crs.model;

import java.util.UUID;

import org.codehaus.jackson.JsonNode;

public class RegistrationViewEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;
	private UUID conferenceId;
	private UUID createdByUserId;
	private String name;
	private JsonNode visibleBlockIds;
	
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
	
	public UUID getCreatedByUserId() {
		return createdByUserId;
	}
	
	public void setCreatedByUserId(UUID createdByUserId) {
		this.createdByUserId = createdByUserId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public JsonNode getVisibleBlockIds() {
		return visibleBlockIds;
	}
	
	public void setVisibleBlockIds(JsonNode visibleBlockIds) {
		this.visibleBlockIds = visibleBlockIds;
	}

}
