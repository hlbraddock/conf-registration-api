package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceCostsEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.queries.ConferenceQueries;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class ConferenceService
{
	Sql2o sql;
	
	org.sql2o.Connection connection;
	
	ConferenceCostsService conferenceCostsService;
	PageService pageService;
    UserService userService;
    
    ConferenceQueries conferenceQueries;
    
    @Inject
	public ConferenceService(Connection connection, ConferenceCostsService conferenceCostsService, PageService pageService, UserService userService)
	{
		this.connection = connection;
		
		this.conferenceCostsService = conferenceCostsService;
		this.pageService = pageService;
        this.userService = userService;
        this.conferenceQueries = new ConferenceQueries();
	}

	public List<ConferenceEntity> fetchAllConferences(CrsApplicationUser crsLoggedInUser)
	{
		return connection.createQuery(conferenceQueries.selectAllForUser())
							.addParameter("contactPersonId", crsLoggedInUser.getId())
							.setAutoDeriveColumnNames(true)
							.executeAndFetch(ConferenceEntity.class);
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
		return connection.createQuery(conferenceQueries.selectById())
							.addParameter("id", id)
							.setAutoDeriveColumnNames(true)
							.executeAndFetchFirst(ConferenceEntity.class);
    }
	
	public void createNewConference(ConferenceEntity newConference, ConferenceCostsEntity newConferenceCosts)
	{   		
		conferenceCostsService.saveNew(newConferenceCosts);
		
		connection.createQuery(conferenceQueries.insert())
        			.addParameter("id", newConference.getId())
        			.addParameter("name", newConference.getName())
        			.addParameter("description", newConference.getDescription())
        			.addParameter("totalSlots", newConference.getTotalSlots())
        			.addParameter("conferenceCostsId", newConference.getConferenceCostsId())
        			.addParameter("eventStartTime", newConference.getEventStartTime())
        			.addParameter("eventEndTime", newConference.getEventEndTime())
        			.addParameter("registrationStartTime", newConference.getRegistrationStartTime())
        			.addParameter("registrationEndTime", newConference.getRegistrationEndTime())
        			.addParameter("contactPersonId", newConference.getContactPersonId())
        			.addParameter("contactPersonName", newConference.getContactPersonName())
        			.addParameter("contactPersonEmail", newConference.getContactPersonEmail())
        			.addParameter("contactPersonPhone", newConference.getContactPersonPhone())
        			.addParameter("locationName", newConference.getLocationName())
        			.addParameter("locationAddress", newConference.getLocationAddress())
        			.addParameter("locationCity", newConference.getLocationCity())
        			.addParameter("locationState", newConference.getLocationState())
        			.addParameter("locationZipCode", newConference.getLocationZipCode())
        			.executeUpdate();
	}

    public void updateConference(ConferenceEntity conferenceToUpdate)
	{
    	connection.createQuery(conferenceQueries.update())
    				.addParameter("id", conferenceToUpdate.getId())
    				.addParameter("name", conferenceToUpdate.getName())
    				.addParameter("description", conferenceToUpdate.getDescription())
    				.addParameter("totalSlots", conferenceToUpdate.getTotalSlots())
    				.addParameter("conferenceCostsId", conferenceToUpdate.getConferenceCostsId())
    				.addParameter("eventStartTime", conferenceToUpdate.getEventStartTime())
    				.addParameter("eventEndTime", conferenceToUpdate.getEventEndTime())
    				.addParameter("registrationStartTime", conferenceToUpdate.getRegistrationStartTime())
    				.addParameter("registrationEndTime", conferenceToUpdate.getRegistrationEndTime())
    				.addParameter("contactPersonId", conferenceToUpdate.getContactPersonId())
    				.addParameter("contactPersonName", conferenceToUpdate.getContactPersonName())
    				.addParameter("contactPersonEmail", conferenceToUpdate.getContactPersonEmail())
    				.addParameter("contactPersonPhone", conferenceToUpdate.getContactPersonPhone())
    				.addParameter("locationName", conferenceToUpdate.getLocationName())
    				.addParameter("locationAddress", conferenceToUpdate.getLocationAddress())
    				.addParameter("locationCity", conferenceToUpdate.getLocationCity())
    				.addParameter("locationState", conferenceToUpdate.getLocationState())
    				.addParameter("locationZipCode", conferenceToUpdate.getLocationZipCode())
    				.executeUpdate();
	}

	public void addPageToConference(ConferenceEntity conferenceToAddPageTo, PageEntity pageToAdd)
	{
		/*create a page id if the client didn't*/
		if(pageToAdd.getId() == null) pageToAdd.setId(UUID.randomUUID());
		pageToAdd.setConferenceId(conferenceToAddPageTo.getId());
		pageService.savePage(pageToAdd);
	}

}