package org.cru.crs.service;

import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.BlockEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BlockServiceTest
{
	org.sql2o.Connection sqlConnection;
	
	@BeforeMethod
	private BlockService getBlockService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		
		return new BlockService(sqlConnection,new AnswerService(sqlConnection));
	}
	
	@Test
	public void testFetchYearInSchoolBlock()
	{
		BlockEntity yearInSchoolBlock = getBlockService().fetchBlockBy(UUID.fromString("a229c854-6989-f658-7c29-b3dd034f6fd1"));
		
		Assert.assertNotNull(yearInSchoolBlock);
		
		Assert.assertEquals(yearInSchoolBlock.getId(), UUID.fromString("a229c854-6989-f658-7c29-b3dd034f6fd1"));
		Assert.assertEquals(yearInSchoolBlock.getPageId(), UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8"));
		Assert.assertEquals(yearInSchoolBlock.getBlockType(), "radioQuestion");
		Assert.assertFalse(yearInSchoolBlock.isAdminOnly());
		Assert.assertTrue(yearInSchoolBlock.isRequired());
		Assert.assertEquals(yearInSchoolBlock.getTitle(), "Year in school");
		Assert.assertEquals(yearInSchoolBlock.getPosition(), 3);
		Assert.assertEquals(actual, expected)
	}
	
	@Test
	public void testFetchBlocksForAboutYourCatPage()
	{
		
	}
	
	@Test
	public void testSaveNewBlockToAboutYourCatPage()
	{
		
	}
	
	@Test
	public void testUpdateCatsNameBlock()
	{
		
	}
	
	@Test
	public void testDeleteYearInSchoolBlock()
	{
		
	}
	
}
