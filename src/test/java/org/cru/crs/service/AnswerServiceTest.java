package org.cru.crs.service;

import java.io.IOException;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.model.AnswerEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups="db-integration-tests")
public class AnswerServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	private EntityManagerFactory emFactory;
	private EntityManager em;

	private AnswerService answerService;

    private UUID originalAnswerUUID = UUID.fromString("441AD805-7AA6-4B20-8315-8F1390DC4A9E");
	private JsonNode originalAnswerValue = jsonNodeFromString("{ \"Name\": \"Alexander Solzhenitsyn\"}");

    @BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		answerService = new AnswerService(em);
	}

	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}

	@Test(groups="db-integration-tests")
	public void getAnswerById()
	{
		AnswerEntity answer = answerService.getAnswerBy(originalAnswerUUID);

		Assert.assertNotNull(answer);
		Assert.assertEquals(answer.getAnswer(), originalAnswerValue);
	}

	@Test(groups="db-integration-tests")
	public void testUpdateAnswer()
	{
		AnswerEntity answer = answerService.getAnswerBy(originalAnswerUUID);

		JsonNode updatedAnswerValue = jsonNodeFromString("{ \"Name\": \"Alexis \"}");

		answer.setAnswer(updatedAnswerValue);

		answerService.updateAnswer(answer);

		AnswerEntity updatedAnswer = em.find(AnswerEntity.class, originalAnswerUUID);

		Assert.assertEquals(updatedAnswer.getAnswer(), updatedAnswerValue);

		updatedAnswer.setAnswer(originalAnswerValue);

		em.merge(updatedAnswer);
	}

	@Test(groups="db-integration-tests")
	public void testDeleteAnswer()
	{
		AnswerEntity answer = new AnswerEntity();

		answer.setId(UUID.randomUUID());
		answer.setAnswer(originalAnswerValue);

		em.persist(answer);
		
		answerService.deleteAnswer(answer);
		
		Assert.assertNull(em.find(AnswerEntity.class, answer.getId()));
	}

	private static JsonNode jsonNodeFromString(String jsonString)
	{
		try
		{
			return (new ObjectMapper()).readTree(jsonString);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}