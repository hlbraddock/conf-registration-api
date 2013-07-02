package org.cru.crs.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class ConferenceServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	private EntityManagerFactory emFactory;
	private EntityManager em;
	
	private ConferenceService conferenceService;
	
	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		conferenceService = new ConferenceService(em);
	}
	
	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}
	
	@Test
	public void fetchAllTheConferences()
	{
		List<ConferenceEntity> allConferences = conferenceService.fetchAllConferences();
		
		Assert.assertNotNull(allConferences);
		Assert.assertFalse(allConferences.isEmpty());
		Assert.assertEquals(allConferences.size(),10);
		
		for(ConferenceEntity conference : allConferences)
		{
			Assert.assertNotNull(conference.getName());
			Assert.assertNotNull(conference.getId());
		}
	}
	
	@Test
	/**
	 * This test currently breaks on Mac b/c of the time offset issue
	 */
	public void fetchConferenceById()
	{
		ConferenceEntity conference = conferenceService.fetchConferenceBy(java.util.UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		
		Assert.assertNotNull(conference);
		
		Assert.assertEquals(conference.getId(), java.util.UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		Assert.assertEquals(conference.getName(), "Northern Michigan Fall Extravaganza");
		Assert.assertEquals(conference.getTotalSlots(), 80);
		Assert.assertEquals(conference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013,8,24,9,32,8));
		Assert.assertEquals(conference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2013,10,2,1,43,14));
		Assert.assertEquals(conference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013,4,10,20,58,35));
		Assert.assertEquals(conference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013,5,22,17,53,8));
	}
	
	@Test
	public void checkPageFetch()
	{
		ConferenceEntity conference = conferenceService.fetchConferenceBy(java.util.UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309"));
		
		Assert.assertNotNull(conference);
		
		Assert.assertNotNull(conference.getPages());
		Assert.assertFalse(conference.getPages().isEmpty());
		Assert.assertEquals(conference.getPages().size(), 3);

		Assert.assertEquals(conference.getPages().get(0).getName(), "Lorem ipsum dolor sit");
		Assert.assertEquals(conference.getPages().get(1).getName(), "Lorem ipsum dolor sit amet, consectetuer");
		Assert.assertEquals(conference.getPages().get(2).getName(), "Lorem ipsum dolor sit amet,");
	}
	
	
}