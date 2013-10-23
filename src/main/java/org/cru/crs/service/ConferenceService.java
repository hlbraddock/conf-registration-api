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
import org.sql2o.Sql2o;

public class ConferenceService
{
	Sql2o sql;
	PageService pageService;
    UserService userService;

    @Inject
	public ConferenceService(EntityManager em, UserService userService, AnswerService answerService)
	{
		this.sql = new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser");
		this.sql.setDefaultColumnMappings(ConferenceEntity.columnMappings);
		
		this.pageService = new PageService(null, this, answerService);
        this.userService = userService;
	}

	public List<ConferenceEntity> fetchAllConferences(CrsApplicationUser crsLoggedInUser)
	{
		return sql.createQuery("SELECT * FROM conferences WHERE contact_person_id = :contactPersonId", false)
					.addParameter("contactPersonId", crsLoggedInUser.getId())
					.executeAndFetch(ConferenceEntity.class);
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
		return sql.createQuery("SELECT * FROM conferences WHERE id = :id", false)
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

//		sql.insert...
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
		sql.createQuery("UPDATE conferences SET name = :name WHERE id = :id")
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