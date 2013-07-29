package org.cru.crs.service;

import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;
import java.util.UUID;

@Test(groups="db-integration-tests")
public class AnswerServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	private EntityManagerFactory emFactory;
	private EntityManager em;

	private AnswerService answerService;

    private UUID originalAnswerUUID = UUID.fromString("441AD805-7AA6-4B20-8315-8F1390DC4A9E");
	private String originalAnswerValue = "{ \"Imya\": \"Alexander Solzhenitsyn\"}";

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

		String updatedAnswerValue = "updated answer";

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
}