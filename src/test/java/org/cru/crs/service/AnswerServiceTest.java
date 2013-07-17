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
	private static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	private EntityManagerFactory emFactory;
	private EntityManager em;

	private AnswerService answerService;

    private UUID originalRegistrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");

    private UUID originalAnswerUUID = UUID.fromString("441AD805-7AA6-4B20-8315-8F1390DC4A9E");
	private String originalAnswerValue = "{ \"Imya\": \"Alexander Solzhenitsyn\"}";

    private RegistrationService registrationService;

    @BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		answerService = new AnswerService(em);
        registrationService = new RegistrationService(em);
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

    // TODO Move to registration service test
	@Test(groups="db-integration-tests")
	public void testCreateNewAnswer()
	{
		AnswerEntity answer = new AnswerEntity();

        RegistrationEntity registrationEntity = registrationService.getRegistrationBy(originalRegistrationUUID);

        answer.setId(UUID.randomUUID());
		answer.setAnswer(originalAnswerValue);

        registrationEntity.getAnswers().add(answer);

        registrationService.updateRegistration(registrationEntity);

		AnswerEntity foundAnswer = em.find(AnswerEntity.class, answer.getId());

		Assert.assertNotNull(foundAnswer);
		Assert.assertEquals(foundAnswer.getId(), answer.getId());
		Assert.assertEquals(foundAnswer.getAnswer(), answer.getAnswer());

		em.remove(foundAnswer);
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