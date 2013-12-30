package org.cru.crs.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonProcessingException;
import org.cru.crs.cdi.SqlConnectionProducer;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.utils.JsonNodeHelper;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AnswerServiceTest
{
	Connection sqlConnection;
	AnswerService answerService;
	
	private static final UUID blockId = UUID.fromString("af60d878-4741-4f21-9d25-231db86e43ee");
	private static final UUID answerId = UUID.fromString("441ad805-7aa6-4b20-8315-8f1390dc4a9e");
	private static final UUID registrationId = UUID.fromString("a2bff4a8-c7dc-4c0a-bb9e-67e6dcB982e7");
	
	@BeforeMethod(alwaysRun=true)
	private void getAnswerService()
	{	
		sqlConnection = new SqlConnectionProducer().getTestSqlConnection();
		answerService = new AnswerService(sqlConnection);
	}
	
	@Test(groups="dbtest")
	public void testGetAnswerById() throws JsonProcessingException, IOException
	{
		AnswerEntity answer = answerService.getAnswerBy(answerId);
		
		Assert.assertNotNull(answer);
		
		Assert.assertEquals(answer.getId(), answerId);
		Assert.assertEquals(answer.getRegistrationId(), registrationId);
		Assert.assertEquals(answer.getBlockId(), blockId);
		Assert.assertEquals(answer.getAnswer(), JsonNodeHelper.toJsonNode("{ \"Name\": \"Alexander Solzhenitsyn\"}"));
	}

	@Test(groups="dbtest")
	public void testGetAllAnswersForRegistration() throws JsonProcessingException, IOException
	{
		List<AnswerEntity> answers = answerService.getAllAnswersForRegistration(registrationId);
		
		Assert.assertNotNull(answers);
		Assert.assertEquals(answers.size(), 1);
		
		Assert.assertEquals(answers.get(0).getId(), answerId);
		Assert.assertEquals(answers.get(0).getBlockId(), blockId);
		Assert.assertEquals(answers.get(0).getRegistrationId(), registrationId);
		Assert.assertEquals(answers.get(0).getAnswer(), JsonNodeHelper.toJsonNode("{ \"Name\": \"Alexander Solzhenitsyn\"}"));
	}

	@Test(groups="dbtest")
	public void testGetAllAnswersForBlock() throws JsonProcessingException, IOException
	{
		List<AnswerEntity> answers = answerService.getAllAnswersForBlock(blockId);
		
		Assert.assertNotNull(answers);
		Assert.assertEquals(answers.size(), 1);
		
		Assert.assertEquals(answers.get(0).getId(), answerId);
		Assert.assertEquals(answers.get(0).getBlockId(), blockId);
		Assert.assertEquals(answers.get(0).getRegistrationId(), registrationId);
		Assert.assertEquals(answers.get(0).getAnswer(), JsonNodeHelper.toJsonNode("{ \"Name\": \"Alexander Solzhenitsyn\"}"));
	}

	@Test(groups="dbtest")
	public void testUpdateAnswer() throws JsonProcessingException, IOException
	{		
		AnswerEntity answerToUpdate = new AnswerEntity();
		answerToUpdate.setId(answerId);
		answerToUpdate.setRegistrationId(registrationId);
		answerToUpdate.setBlockId(blockId);
		answerToUpdate.setAnswer(JsonNodeHelper.toJsonNode("{ \"Name\": \"Alex Solzhenitsyn\"}"));
		
		try
		{
			answerService.updateAnswer(answerToUpdate);
			
			AnswerEntity retrievedAnswer = answerService.getAnswerBy(answerId);
			
			Assert.assertNotNull(retrievedAnswer);
			Assert.assertEquals(answerService.getAllAnswersForBlock(blockId).size(), 1);
			Assert.assertEquals(answerService.getAllAnswersForRegistration(registrationId).size(), 1);
			
			Assert.assertEquals(retrievedAnswer.getId(), answerId);
			Assert.assertEquals(retrievedAnswer.getBlockId(), blockId);
			Assert.assertEquals(retrievedAnswer.getRegistrationId(), registrationId);
			Assert.assertEquals(retrievedAnswer.getAnswer(), JsonNodeHelper.toJsonNode("{ \"Name\": \"Alex Solzhenitsyn\"}"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest")
	public void testInsertAnswer() throws JsonProcessingException, IOException
	{
		AnswerEntity newAnswer = new AnswerEntity();
		UUID id = UUID.randomUUID();
		
		newAnswer.setId(id);
		newAnswer.setRegistrationId(registrationId);
		newAnswer.setBlockId(UUID.fromString("f774ea5c-8e44-25dc-9169-2f141c57e3ae"));
		newAnswer.setAnswer(JsonNodeHelper.toJsonNode("{ \"Email\": \"alex.sol@gmail.com\"}"));
				
		try
		{
			answerService.insertAnswer(newAnswer);
			
			AnswerEntity retrievedAnswer = answerService.getAnswerBy(id);
			
			Assert.assertNotNull(retrievedAnswer);
			Assert.assertEquals(answerService.getAllAnswersForRegistration(registrationId).size(), 2);
			Assert.assertEquals(answerService.getAllAnswersForBlock(UUID.fromString("f774ea5c-8e44-25dc-9169-2f141c57e3ae")).size(), 1);
			
			Assert.assertEquals(retrievedAnswer.getId(), id);
			Assert.assertEquals(retrievedAnswer.getBlockId(), UUID.fromString("f774ea5c-8e44-25dc-9169-2f141c57e3ae"));
			Assert.assertEquals(retrievedAnswer.getRegistrationId(), registrationId);
			Assert.assertEquals(retrievedAnswer.getAnswer(), JsonNodeHelper.toJsonNode("{ \"Email\": \"alex.sol@gmail.com\"}"));
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest")
	public void testDeleteAnswer()
	{
		try
		{
			answerService.deleteAnswer(answerId);
			
			Assert.assertNull(answerService.getAnswerBy(answerId));
			Assert.assertTrue(answerService.getAllAnswersForBlock(blockId).isEmpty());
			Assert.assertTrue(answerService.getAllAnswersForRegistration(registrationId).isEmpty());
		}
		finally
		{
			sqlConnection.rollback();
		}
		
	}

	@Test(groups="dbtest")
	public void testDeleteAnswersByBlockId()
	{
		try
		{
			answerService.deleteAnswersByBlockId(blockId);
			
			Assert.assertNull(answerService.getAnswerBy(answerId));
			Assert.assertTrue(answerService.getAllAnswersForBlock(blockId).isEmpty());
			Assert.assertTrue(answerService.getAllAnswersForRegistration(registrationId).isEmpty());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}

	@Test(groups="dbtest")
	public void testDeleteAnswersByRegistrationId()
	{
		try
		{
			answerService.deleteAnswersByRegistrationId(registrationId);
			
			Assert.assertNull(answerService.getAnswerBy(answerId));
			Assert.assertTrue(answerService.getAllAnswersForBlock(blockId).isEmpty());
			Assert.assertTrue(answerService.getAllAnswersForRegistration(registrationId).isEmpty());
		}
		finally
		{
			sqlConnection.rollback();
		}
	}
}
