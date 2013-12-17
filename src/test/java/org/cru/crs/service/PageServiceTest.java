package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.PageEntity;
import org.cru.crs.utils.ConferenceInfo;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PageServiceTest
{
	Connection sqlConnection;
	PageService pageService;
	
	@BeforeMethod
	private void setupConnectionAndService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		pageService = new PageService(sqlConnection,new BlockService(sqlConnection, new AnswerService(sqlConnection)));
	}
	
	@Test
	public void testFetchAboutYourCatPage()
	{
		PageEntity pageEntity = pageService.fetchPageBy(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		
		Assert.assertNotNull(pageEntity);
		Assert.assertEquals(pageEntity.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		Assert.assertEquals(pageEntity.getPosition(), 1);
		Assert.assertEquals(pageEntity.getTitle(), "About your cat");
		Assert.assertEquals(pageEntity.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
	}
	
	@Test
	public void testFetchPagesForNorthernMichiganConference()
	{
		List<PageEntity> pagesForNorthernMichigan = pageService.fetchPagesForConference(ConferenceInfo.Id.NorthernMichigan);
		
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
		PageEntity newPage = new PageEntity();
		UUID id = UUID.randomUUID();
		
		newPage.setId(id);
		newPage.setConferenceId(ConferenceInfo.Id.NorthernMichigan);
		newPage.setPosition(3);
		newPage.setTitle("Brand new page");
		
		try
		{
			pageService.savePage(newPage);
			
			PageEntity savedPage = pageService.fetchPageBy(id);
			
			Assert.assertNotNull(savedPage);
			Assert.assertEquals(savedPage.getId(), id);
			Assert.assertEquals(savedPage.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
			Assert.assertEquals(savedPage.getPosition(), 3);
			Assert.assertEquals(savedPage.getTitle(), "Brand new page");
			
			Assert.assertEquals(pageService.fetchPagesForConference(ConferenceInfo.Id.NorthernMichigan).size(), 4);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void updateAboutYouPage()
	{
		PageEntity updatedAboutYouPage = new PageEntity();
		
		updatedAboutYouPage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		updatedAboutYouPage.setConferenceId(ConferenceInfo.Id.NorthernMichigan);
		updatedAboutYouPage.setPosition(0);
		updatedAboutYouPage.setTitle("About someone else");
		
		try
		{
			pageService.updatePage(updatedAboutYouPage);
			
			PageEntity updatedPage = pageService.fetchPageBy(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			
			Assert.assertNotNull(updatedPage);
			Assert.assertEquals(updatedPage.getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			Assert.assertEquals(updatedPage.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
			Assert.assertEquals(updatedPage.getPosition(), 0);
			Assert.assertEquals(updatedPage.getTitle(), "About someone else");
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void deleteHobbiesPage()
	{
		try
		{
			pageService.deletePage(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5"));
			
			Assert.assertNull(pageService.fetchPageBy(UUID.fromString("7dae078f-a131-471e-bb70-5156b62ddea5")));
			Assert.assertEquals(pageService.fetchPagesForConference(ConferenceInfo.Id.NorthernMichigan).size(), 2);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
