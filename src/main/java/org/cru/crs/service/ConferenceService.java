package org.cru.crs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cru.crs.api.model.Page;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.UserEntity;
import org.cru.crs.utils.CollectionUtils;

public class ConferenceService
{
	EntityManager em;
    UserService userService;
	AnswerService answerService;

    @Inject
	public ConferenceService(EntityManager em, UserService userService, AnswerService answerService)
	{
		this.em = em;
        this.userService = userService;
		this.answerService = answerService;
	}

	public List<ConferenceEntity> fetchAllConferences(CrsApplicationUser crsLoggedInUser)
	{
		return em.createQuery("SELECT conf FROM ConferenceEntity conf " +
								"WHERE conf.contactUser = :crsAppUserId", ConferenceEntity.class)
							.setParameter("crsAppUserId", crsLoggedInUser.getId())
				 			.getResultList();
	}

	public ConferenceEntity fetchConferenceBy(UUID id)
	{
        return em.find(ConferenceEntity.class, id);
    }
	
	public void createNewConference(ConferenceEntity newConference, CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		if(crsLoggedInUser == null || crsLoggedInUser.isCrsAuthenticatedOnly())
		{
			throw new UnauthorizedException();
		}
		
		newConference.setContactUser(crsLoggedInUser.getId());

        newConference = setInitialContactPersonDetailsBasedOn(crsLoggedInUser, newConference);

		em.persist(newConference);
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
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferenceToUpdate.getContactUser()))
		{
			throw new UnauthorizedException();
		}
        ConferenceEntity originalConference = em.find(ConferenceEntity.class,conferenceToUpdate.getId());

        /*
         * The updating of pages below will cause an exception at the database level if a new page is added.  This is
         * because JPA says that conference ID on a page is not insertable/updatable, so when it tries to insert
         * a new page, postgres complains b/c it has a null value on a foreign key.
         */

        Set<Page> pagesOnOriginalConference = new HashSet<Page>();
        pagesOnOriginalConference.addAll(Page.fromJpa(originalConference.getPages()));

        Set<Page> pagesOnUpdatedConference = new HashSet<Page>();
        pagesOnUpdatedConference.addAll(Page.fromJpa(conferenceToUpdate.getPages()));

        Set<Page> addedPages = CollectionUtils.firstNotFoundInSecond(pagesOnUpdatedConference, pagesOnOriginalConference);

        for(Page page : addedPages)
        {
            addPageToConference(originalConference, page.toJpaPageEntity(), crsLoggedInUser);
        }

        /*
         * So that blocks don't get deleting when moving them to a preceding page, update pages
         * one by one and flush to the database between moving them.  See Github issue 39 and PR 42 for context
         */
        //can't inject a PageService, because PageService injects this class.
        PageService pageService = new PageService(em,this, new AnswerService(em));

		// delete answers on pages update
		ConferenceEntity currentConference = fetchConferenceBy(conferenceToUpdate.getId());
		pageService.deleteAnswersOnPagesUpdate(currentConference.getPages(), conferenceToUpdate.getPages());

		for(PageEntity page : conferenceToUpdate.getPages())
        {
			//While it's true that the conferenceId of a page can't be altered by updating the page
			//it's possible the client did not set the conference of the page, and a verification 
			//call inside the service will puke w/o its being set.
			page.setConferenceId(conferenceToUpdate.getId());
            pageService.updatePage(page, crsLoggedInUser, false);
            em.flush();
        }

		em.merge(conferenceToUpdate);
	}

	public void addPageToConference(ConferenceEntity conferenceToAddPageTo, PageEntity pageToAdd,  CrsApplicationUser crsLoggedInUser) throws UnauthorizedException
	{
		/*if there is no user ID, or the conference belongs to a different user, the return a 401 - Unauthorized*/
		if(crsLoggedInUser == null || !crsLoggedInUser.getId().equals(conferenceToAddPageTo.getContactUser()))
		{
			throw new UnauthorizedException();
		}
		
		/*create a page id if the client didn't*/
		if(pageToAdd.getId() == null) pageToAdd.setId(UUID.randomUUID());
		
		conferenceToAddPageTo.getPages().add(pageToAdd.setConferenceId(conferenceToAddPageTo.getId()));

        em.flush();
	}

}