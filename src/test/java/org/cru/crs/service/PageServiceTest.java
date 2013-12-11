package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PageEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PageServiceTest
{
	org.sql2o.Connection sqlConnection;
	
	@BeforeMethod
	private PageService getPageService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		return new PageService(sqlConnection,new BlockService(sqlConnection, new AnswerService(sqlConnection)));
	}
	
	@Test
	public void testFetchAboutYourCatPage()
	{
		PageEntity pageEntity = getPageService().fetchPageBy(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		
		Assert.assertNotNull(pageEntity);
		Assert.assertEquals(pageEntity.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		Assert.assertEquals(pageEntity.getPosition(), 1);
		Assert.assertEquals(pageEntity.getTitle(), "About your cat");
		Assert.assertEquals(pageEntity.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
	}
	
	@Test
	public void testFetchPagesForNorthernMichiganConference()
	{
		List<PageEntity> pagesForNorthernMichigan = getPageService().fetchPagesForConference(ConferenceInfo.Id.NorthernMichigan);
		
		Assert.assertNotNull(pagesForNorthernMichigan);
		Assert.assertEquals(pagesForNorthernMichigan.size(), 3);
		
		for(PageEntity page : pagesForNorthernMichigan)
		{
			if(page.getPosition() == 0)
			{
				Assert.assertEquals(page.getTitle(), "About you");
			}
			else if(page.getPosition() == 1)
			{
				Assert.assertEquals(page.getTitle(), "About your cat");
			}
			else if(page.getPosition() == 2)
			{
				Assert.assertEquals(page.getTitle(), "Hobbies and activities");
			}
			else
			{
				Assert.fail();
			}
		}
	}
	
	@Test
	public void testSaveNewPage()
	{
		
	}
	
	@Test
	public void updateAboutYouPage()
	{

	}
	
	@Test
	public void deleteHobbiesPage()
	{
		try
		{
			PageService pageService = getPageService();
			pageService.deletePage(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));
			
			Assert.assertNull(pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5")));
			Assert.assertEquals(pageService.fetchPagesForConference(ConferenceInfo.Id.NorthernMichigan).size(), 2);
		
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void addBlockToAboutYourCatPage()
	{
		
	}
}
