package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.queries.ConferenceQueries;
import org.sql2o.Connection;
import org.testng.collections.Lists;

@RequestScoped
public class ConferenceService
{
	org.sql2o.Connection sqlConnection;
	
	ConferenceCostsService conferenceCostsService;
	PageService pageService;
    UserService userService;
    PermissionService permissionService;
    
    ConferenceQueries conferenceQueries;
    
	/*Weld requires a default no args constructor to proxy this object*/
    public ConferenceService(){}
    
    @Inject
	public ConferenceService(Connection connection, ConferenceCostsService conferenceCostsService, PageService pageService, UserService userService, PermissionService permissionService)
	{
		this.sqlConnection = connection;
		
		this.conferenceCostsService = conferenceCostsService;
		this.pageService = pageService;
        this.userService = userService;
        this.permissionService = permissionService;
        
        this.conferenceQueries = new ConferenceQueries();
	}

	public List<ConferenceEntity> fetchAllConferencesForUser(UUID userId)
	{
		List<PermissionEntity> permissionsForUser = permissionService.getPermissionsForUser(userId);
		
		List<ConferenceEntity> conferencesForUser = Lists.newArrayList();
		
		for(PermissionEntity permission : permissionsForUser)
		{
			if(permission.getPermissionLevel().isViewOrAbove())
			{
				conferencesForUser.add(fetchConferenceBy(permission.getConferenceId()));
			}
		}
		
		return conferencesForUser;
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
		return sqlConnection.createQuery(conferenceQueries.selectById())
							.addParameter("id", id)
							.setAutoDeriveColumnNames(true)
							.executeAndFetchFirst(ConferenceEntity.class);
    }
	
	public void createNewConference(ConferenceEntity newConference, ConferenceCostsEntity newConferenceCosts)
	{		
		conferenceCostsService.saveNew(newConferenceCosts);
		
		sqlConnection.createQuery(conferenceQueries.insert())
        				.addParameter("id", newConference.getId())
        				.addParameter("name", newConference.getName())
        				.addParameter("description", newConference.getDescription())
        				.addParameter("totalSlots", newConference.getTotalSlots())
        				.addParameter("conferenceCostsId", newConference.getConferenceCostsId())
        				.addParameter("eventStartTime", newConference.getEventStartTime())
        				.addParameter("eventEndTime", newConference.getEventEndTime())
        				.addParameter("registrationStartTime", newConference.getRegistrationStartTime())
        				.addParameter("registrationEndTime", newConference.getRegistrationEndTime())
        				.addParameter("contactPersonName", newConference.getContactPersonName())
        				.addParameter("contactPersonEmail", newConference.getContactPersonEmail())
        				.addParameter("contactPersonPhone", newConference.getContactPersonPhone())
        				.addParameter("locationName", newConference.getLocationName())
        				.addParameter("locationAddress", newConference.getLocationAddress())
        				.addParameter("locationCity", newConference.getLocationCity())
        				.addParameter("locationState", newConference.getLocationState())
        				.addParameter("locationZipCode", newConference.getLocationZipCode())
        				.addParameter("requireLogin", newConference.isRequireLogin())
        				.executeUpdate();
	}

	public void updateConference(ConferenceEntity conferenceToUpdate)
	{
		sqlConnection.createQuery(conferenceQueries.update())
    					.addParameter("id", conferenceToUpdate.getId())
    					.addParameter("name", conferenceToUpdate.getName())
    					.addParameter("description", conferenceToUpdate.getDescription())
    					.addParameter("totalSlots", conferenceToUpdate.getTotalSlots())
    					.addParameter("conferenceCostsId", conferenceToUpdate.getConferenceCostsId())
    					.addParameter("eventStartTime", conferenceToUpdate.getEventStartTime())
    					.addParameter("eventEndTime", conferenceToUpdate.getEventEndTime())
    					.addParameter("registrationStartTime", conferenceToUpdate.getRegistrationStartTime())
    					.addParameter("registrationEndTime", conferenceToUpdate.getRegistrationEndTime())
    					.addParameter("contactPersonName", conferenceToUpdate.getContactPersonName())
    					.addParameter("contactPersonEmail", conferenceToUpdate.getContactPersonEmail())
    					.addParameter("contactPersonPhone", conferenceToUpdate.getContactPersonPhone())
    					.addParameter("locationName", conferenceToUpdate.getLocationName())
    					.addParameter("locationAddress", conferenceToUpdate.getLocationAddress())
    					.addParameter("locationCity", conferenceToUpdate.getLocationCity())
    					.addParameter("locationState", conferenceToUpdate.getLocationState())
    					.addParameter("locationZipCode", conferenceToUpdate.getLocationZipCode())
    					.addParameter("requireLogin", conferenceToUpdate.isRequireLogin())
    					.executeUpdate();
	}
}