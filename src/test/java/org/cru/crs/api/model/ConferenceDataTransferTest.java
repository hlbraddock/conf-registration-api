package org.cru.crs.api.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConferenceDataTransferTest
{

	@Test(groups="unittest")
	public void testWebEntityToJpaEntity()
	{
		Conference conference = new Conference();
		
		conference.setContactUser(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		conference.setEventEndTime(DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		conference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		conference.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		conference.setName("Awesome fall retreat");
		conference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		conference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		conference.setTotalSlots(150);
        conference.setLocationName("Farmville Camp");
        conference.setLocationAddress("1234 Farmers St");
        conference.setLocationCity("Farmville");
		conference.setLocationState("TX");
        conference.setLocationZipCode("23451");

        ConferenceEntity jpaConference = conference.toJpaConferenceEntity();
		
		Assert.assertNotNull(jpaConference);
		Assert.assertEquals(jpaConference.getContactPersonId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		Assert.assertEquals(jpaConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		Assert.assertEquals(jpaConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		Assert.assertEquals(jpaConference.getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		Assert.assertEquals(jpaConference.getName(), "Awesome fall retreat");
		Assert.assertEquals(jpaConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		Assert.assertEquals(jpaConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		Assert.assertEquals(jpaConference.getTotalSlots(), 150);
        Assert.assertEquals(jpaConference.getLocationName(), "Farmville Camp");
        Assert.assertEquals(jpaConference.getLocationAddress(), "1234 Farmers St");
        Assert.assertEquals(jpaConference.getLocationCity(), "Farmville");
        Assert.assertEquals(jpaConference.getLocationState(), "TX");
        Assert.assertEquals(jpaConference.getLocationZipCode(), "23451");
	}
	
	@Test(groups="unittest")
	public void testJpaEntityToWebEntity()
	{
		ConferenceEntity conference = new ConferenceEntity();
		
		conference.setContactPersonId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		conference.setEventEndTime(DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		conference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		conference.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		conference.setName("Awesome fall retreat");
		conference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		conference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		conference.setTotalSlots(150);
		
		Conference webConference = Conference.fromJpa(conference);
		
		Assert.assertNotNull(webConference);
		Assert.assertEquals(webConference.getContactUser(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1234"));
		Assert.assertEquals(webConference.getEventEndTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 4, 12, 0, 0));
		Assert.assertEquals(webConference.getEventStartTime(), DateTimeCreaterHelper.createDateTime(2013, 9, 1, 4, 0, 0));
		Assert.assertEquals(webConference.getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe5678"));
		Assert.assertEquals(webConference.getName(), "Awesome fall retreat");
		Assert.assertEquals(webConference.getRegistrationEndTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 30, 12, 0, 0));
		Assert.assertEquals(webConference.getRegistrationStartTime(), DateTimeCreaterHelper.createDateTime(2013, 8, 12, 17, 30, 10));
		Assert.assertEquals(webConference.getTotalSlots(), 150);
	}
	
	@Test(groups="unittest")
	public void testJpaEntityWithPagesToWebEntityWithPages() throws JsonParseException, JsonProcessingException, IOException
	{
		ConferenceEntity jpaConference = new ConferenceEntity();
		
		PageEntity page1 = new PageEntity();
		PageEntity page2 = new PageEntity();
		PageEntity page3 = new PageEntity();
		
		BlockEntity block1 = new BlockEntity();
		BlockEntity block2 = new BlockEntity();
		
		page1.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1111"));
		page1.setConferenceId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffecccc"));
		page1.setTitle("About you");
		page1.setPosition(0);
		
		page2.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe2222"));
		page2.setConferenceId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffecccc"));
		page2.setTitle("About me");
		page2.setPosition(1);

		page3.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe3333"));
		page3.setConferenceId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffecccc"));
		page3.setTitle("About your cat");
		page3.setPosition(2);

		List<PageEntity> pages = new ArrayList<PageEntity>();
		pages.add(page1);
		pages.add(page2);
		pages.add(page3);
		
		block1.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1122"));
		block1.setPageId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1111"));
		block1.setAdminOnly(false);
		block1.setBlockType("multipleChoice");
		block1.setContent(new ObjectMapper().getJsonFactory().createJsonParser("{\"Year in college\" : \"Sophomore\"}").readValueAsTree());
		
		block2.setId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1133"));
		block2.setPageId(UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1111"));
		block2.setAdminOnly(false);
		block2.setBlockType("text");
		block2.setContent(new ObjectMapper().getJsonFactory().createJsonParser("{\"Cats name\" : \"Reese\"}").readValueAsTree());
		
		List<BlockEntity> blocks = new ArrayList<BlockEntity>();
		blocks.add(block1);
		blocks.add(block2);
		
//		page1.setBlocks(blocks);
		
//		jpaConference.setPages(pages);
		
		Conference conferenceWithPages = Conference.fromJpaWithPages(jpaConference);
		
		List<Page> webRegistrationPages = conferenceWithPages.getRegistrationPages();
		
		Assert.assertNotNull(webRegistrationPages);
		Assert.assertEquals(webRegistrationPages.size(), 3);
		
		Assert.assertEquals(webRegistrationPages.get(0).getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1111"));
		Assert.assertEquals(webRegistrationPages.get(0).getTitle(), "About you");
		Assert.assertEquals(webRegistrationPages.get(0).getPosition(), 0);
		Assert.assertEquals(webRegistrationPages.get(0).getConferenceId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffecccc"));
		
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(0).getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1122"));
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(0).getPageId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1111"));
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(0).getType(), "multipleChoice");
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(0).getContent().get("Year in college").getTextValue(), "Sophomore");
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(1).getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1133"));
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(1).getPageId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe1111"));
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(1).getType(), "text");
		Assert.assertEquals(webRegistrationPages.get(0).getBlocks().get(1).getContent().get("Cats name").getTextValue(), "Reese");
		
		Assert.assertEquals(webRegistrationPages.get(1).getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe2222"));
		Assert.assertEquals(webRegistrationPages.get(1).getTitle(), "About me");
		Assert.assertEquals(webRegistrationPages.get(1).getPosition(), 1);
		Assert.assertEquals(webRegistrationPages.get(1).getConferenceId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffecccc"));

		Assert.assertEquals(webRegistrationPages.get(2).getId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffe3333"));
		Assert.assertEquals(webRegistrationPages.get(2).getTitle(), "About your cat");
		Assert.assertEquals(webRegistrationPages.get(2).getPosition(), 2);
		Assert.assertEquals(webRegistrationPages.get(2).getConferenceId(), UUID.fromString("abcd1234-abcd-1234-effe-abcdeffecccc"));

	}
}
