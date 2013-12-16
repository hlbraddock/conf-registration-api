package org.cru.crs.service;

import java.util.List;
import java.util.UUID;

import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.BlockEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TODO: figure out how to test JSON node
 * @author ryancarlson
 *
 */
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
	}
	
	@Test
	public void testFetchBlocksForAboutYourCatPage()
	{
		List<BlockEntity> blocksForAboutYourCat = getBlockService().fetchBlocksForPage(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		
		Assert.assertNotNull(blocksForAboutYourCat);
		
		Assert.assertEquals(blocksForAboutYourCat.size(), 4);
		
		for(BlockEntity block : blocksForAboutYourCat)
		{
			if(block.getPosition() == 0)
			{
				Assert.assertEquals(block.getTitle(), "We love pets");
			}
			else if(block.getPosition() == 1)
			{
				Assert.assertEquals(block.getTitle(), "Kittehs name");	
			}
			else if(block.getPosition() == 2)
			{
				Assert.assertEquals(block.getTitle(), "Mah kitteh iz... (check all that apply)");
			}
			else if(block.getPosition() == 3)
			{
				Assert.assertEquals(block.getTitle(), "Preferred cat food brand (or cheezburgerz)");
			}
			
		}
		
	}
	
	@Test
	public void testSaveNewBlockToAboutYourCatPage()
	{
		BlockService blockService = getBlockService();
		
		BlockEntity newBlock = new BlockEntity();
		UUID id = UUID.randomUUID();
		
		newBlock.setId(id);
		newBlock.setTitle("Something else about kitteh");
		newBlock.setBlockType("textQuestion");
		newBlock.setAdminOnly(false);
		newBlock.setRequired(false);
		newBlock.setPageId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		newBlock.setPosition(4);
		
		try
		{
			blockService.saveBlock(newBlock);
			
			BlockEntity retrievedBlock = blockService.fetchBlockBy(id);
			
			Assert.assertNotNull(retrievedBlock);
			Assert.assertEquals(retrievedBlock.getId(), id);
			Assert.assertEquals(retrievedBlock.getTitle(),"Something else about kitteh");
			Assert.assertEquals(retrievedBlock.getBlockType(), "textQuestion");
			Assert.assertFalse(retrievedBlock.isAdminOnly());
			Assert.assertFalse(retrievedBlock.isRequired());
			Assert.assertEquals(retrievedBlock.getPageId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			Assert.assertEquals(retrievedBlock.getPosition(), 4);
			
			Assert.assertEquals(blockService.fetchBlocksForPage(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c")).size(), 5);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testUpdateCatsNameBlock()
	{
		//DDA45720-DE87-C419-933A-0187-12B152DC
		
		BlockService blockService = getBlockService();
		
		BlockEntity updatedCatsNameBlock = new BlockEntity();
		
		updatedCatsNameBlock.setId(UUID.fromString("dda45720-de87-c419-933a-018712b152dc"));
		updatedCatsNameBlock.setPageId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
		updatedCatsNameBlock.setPosition(1);
		updatedCatsNameBlock.setTitle("Catz name");
		updatedCatsNameBlock.setAdminOnly(true);
		updatedCatsNameBlock.setRequired(false);
		updatedCatsNameBlock.setBlockType("nameQuestion");
		
		try
		{
			blockService.updateBlock(updatedCatsNameBlock);
			
			BlockEntity updatedBlock = blockService.fetchBlockBy(UUID.fromString("dda45720-de87-c419-933a-018712b152dc"));
			
			Assert.assertNotNull(updatedBlock);
			Assert.assertEquals(updatedBlock.getId(), UUID.fromString("dda45720-de87-c419-933a-018712b152dc"));
			Assert.assertEquals(updatedBlock.getPageId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0b27c"));
			Assert.assertEquals(updatedBlock.getPosition(), 1);
			Assert.assertEquals(updatedBlock.getTitle(), "Catz name");
			Assert.assertTrue(updatedBlock.isAdminOnly());
			Assert.assertFalse(updatedBlock.isRequired());
			Assert.assertEquals(updatedBlock.getBlockType(), "nameQuestion");
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
	@Test
	public void testDeleteYearInSchoolBlock()
	{
		try
		{
			BlockService blockService = getBlockService();
			blockService.deleteBlock(UUID.fromString("a229c854-6989-f658-7c29-b3dd034f6fd1"));
			
			Assert.assertNull(blockService.fetchBlockBy(UUID.fromString("a229c854-6989-f658-7c29-b3dd034f6fd1")));
			Assert.assertEquals(blockService.fetchBlocksForPage(UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8")).size(), 3);
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
	
}
