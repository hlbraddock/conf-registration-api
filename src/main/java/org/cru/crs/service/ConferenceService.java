package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.model.queries.ConferenceQueries;
import org.cru.crs.model.queries.EntityColumnMappings;
import org.sql2o.Sql2o;

public class ConferenceService
{
	Sql2o sql;
	PageService pageService;
    UserService userService;
    ConferenceQueries conferenceQueries;
    
    @Inject
	public ConferenceService(EntityManager em, UserService userService, AnswerService answerService)
	{
		this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(EntityColumnMappings.get(ConferenceEntity.class));
		
		this.pageService = new PageService(null, this, answerService);
        this.userService = userService;
        this.conferenceQueries = new ConferenceQueries();
	}

	public List<ConferenceEntity> fetchAllConferences(CrsApplicationUser crsLoggedInUser)
	{
		return sql.createQuery(conferenceQueries.selectAllForUser(), false)
					.addParameter("contactPersonId", crsLoggedInUser.getId())
					.executeAndFetch(ConferenceEntity.class);
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
		return sql.createQuery(conferenceQueries.selectById(), false)
					.addParameter("id", id)
					.executeAndFetchFirst(ConferenceEntity.class);
    }
	
	public void createNewConference(ConferenceEntity newConference, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		if(crsLoggedInUser == null || crsLoggedInUser.isCrsAuthenticatedOnly())
		{
			throw new UnauthorizedException();
		}
		
		newConference.setContactPersonId(crsLoggedInUser.getId());

        newConference = setInitialContactPersonDetailsBasedOn(crsLoggedInUser, newConference);

        sql.createQuery("INSERT INTO conference_costs(id) VALUES (:id)", false)
        		.addParameter("id", newConference.getId())
        		.executeUpdate();
        
        sql.createQuery(conferenceQueries.insert(), false)
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

    private ConferenceEntity setInitialContactPersonDetailsBasedOn(CrsApplicationUser crsLoggedInUser, ConferenceEntity newConference)
    {
        UserEntity user = userService.fetchUserBy(crsLoggedInUser.getId());
        newConference.setContactPersonName(user.getFirstName() + " " + user.getLastName());
        newConference.setContactPersonEmail(user.getEmailAddress());
        newConference.setContactPersonPhone(user.getPhoneNumber());

        return newConference;
    }

    public void updateConference(ConferenceEntity conferenceToUpdate,  CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferenceToUpdate.getContactPersonId()))
		{
			throw new UnauthorizedException();
		}
        
		/*content and conferenceCostsBlocksId omitted for now*/
		sql.createQuery(conferenceQueries.update())
				.addParameter("name", conferenceToUpdate.getName())
				.executeUpdate();
	}

	public void addPageToConference(ConferenceEntity conferenceToAddPageTo, PageEntity pageToAdd,  CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		/*if there is no user ID, or the conference belongs to a different user, the return a 401 - Unauthorized*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferenceToAddPageTo.getContactPersonId()))
		{
			throw new UnauthorizedException();
		}
		
		/*create a page id if the client didn't*/
		if(pageToAdd.getId() == null) pageToAdd.setId(UUID.randomUUID());
		pageToAdd.setConferenceId(conferenceToAddPageTo.getId());
		pageService.savePage(pageToAdd);
	}

}