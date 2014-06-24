package org.cru.crs.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.cru.crs.model.RegistrationViewEntity;

import java.util.UUID;

public class RegistrationView implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private UUID conferenceId;
	private UUID createdByUserId;
	private String name;
	private JsonNode visibleBlockIds;
	
	public RegistrationViewEntity toDbDataViewEntity() {
		RegistrationViewEntity registrationViewEntity = new RegistrationViewEntity();
		
		registrationViewEntity.setId(id);
		registrationViewEntity.setConferenceId(conferenceId);
		registrationViewEntity.setCreatedByUserId(createdByUserId);
		registrationViewEntity.setName(name);
		registrationViewEntity.setVisibleBlockIds(visibleBlockIds);
		
		return registrationViewEntity;
	}
	

	public static RegistrationView fromDb(RegistrationViewEntity dbRegistrationView) {
		RegistrationView registrationView = new RegistrationView();
		
		registrationView.id = dbRegistrationView.getId();
		registrationView.conferenceId = dbRegistrationView.getConferenceId();
		registrationView.createdByUserId = dbRegistrationView.getCreatedByUserId();
		registrationView.name = dbRegistrationView.getName();
		registrationView.visibleBlockIds = dbRegistrationView.getVisibleBlockIds();
		
		return registrationView;
	}
	
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
