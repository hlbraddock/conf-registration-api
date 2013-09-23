package org.cru.crs.service;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.auth.authz.AuthorizationService;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.RegistrationEntity;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.UUID;

@Test(groups="db-integration-tests")
public class RegistrationServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	private EntityManagerFactory emFactory;
	private EntityManager em;

    private RegistrationService registrationService;
    private ConferenceService conferenceService;

	private UUID originalRegistrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");
	private UUID originalUserUUID = UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898");
	private UUID originalConferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

	private UUID adminUserUUID = UUID.fromString("dbc6a808-d7bc-4d92-967c-d82d9d312898");

	private CrsApplicationUser crsApplicationUser;
	private CrsApplicationUser adminCrsApplicationUser;
    private JsonNode originalAnswerValue = jsonNodeFromString("{ \"Name\": \"Alexander Solzhenitsyn\"}");

    @BeforeMethod
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();

        registrationService = new RegistrationService(em, new AuthorizationService());
        conferenceService = new ConferenceService(em, new UserService(em));

		crsApplicationUser = new CrsApplicationUser(originalUserUUID, AuthenticationProviderType.FACEBOOK, null/*username will go here*/);
		adminCrsApplicationUser = new CrsApplicationUser(adminUserUUID, AuthenticationProviderType.FACEBOOK, null/*username will go here*/);
	}

	@AfterMethod
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}

	@Test(groups="db-integration-tests")
	public void getRegistrationById() throws UnauthorizedException
	{
		RegistrationEntity registration = registrationService.getRegistrationBy(originalRegistrationUUID, crsApplicationUser);

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getUserId(), originalUserUUID);
		Assert.assertEquals(registration.getConference().getId(), originalConferenceUUID);
	}

	@Test(groups="db-integration-tests")
	public void getRegistrationByConferenceIdUserId() throws UnauthorizedException
	{
		RegistrationEntity registration = registrationService.getRegistrationByConferenceIdUserId(originalConferenceUUID, originalUserUUID, crsApplicationUser);

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getUserId(), originalUserUUID);
		Assert.assertEquals(registration.getConference().getId(), originalConferenceUUID);
	}

	@Test(groups="db-integration-tests")
	public void testCreateNewRegistration() throws UnauthorizedException
	{
		RegistrationEntity registration = new RegistrationEntity();

		ConferenceEntity conference = conferenceService.fetchConferenceBy(originalConferenceUUID);

		String someUserAuthProviderId = "ABC4C1B2-0CC1-89F7-9F4B-6BC3E0DB5789";

		UUID someUserUUID = UUID.fromString("CACE5EBF-9DAB-3F7C-210D-E2732C89FD2C");

		registration.setId(UUID.randomUUID());
		registration.setConference(conference);
		registration.setUserId(someUserUUID);

		CrsApplicationUser someCrsApplicationUser = new CrsApplicationUser(someUserUUID, AuthenticationProviderType.FACEBOOK, null/*username will go here*/);

		registrationService.createNewRegistration(registration, someCrsApplicationUser);

		RegistrationEntity foundRegistration = em.find(RegistrationEntity.class, registration.getId());

		Assert.assertNotNull(foundRegistration);
		Assert.assertEquals(foundRegistration.getId(), registration.getId());
		Assert.assertEquals(foundRegistration.getUserId(), registration.getUserId());
		Assert.assertEquals(foundRegistration.getConference().getId(), registration.getConference().getId());

		em.remove(foundRegistration);
	}

	@Test(groups="db-integration-tests", expectedExceptions = UnauthorizedException.class)
	public void testCreateMultipleNewRegistration() throws UnauthorizedException
	{
		RegistrationEntity registration = new RegistrationEntity();

        ConferenceEntity conference = conferenceService.fetchConferenceBy(originalConferenceUUID);

        registration.setId(UUID.randomUUID());
        registration.setConference(conference);
		registration.setUserId(originalUserUUID);

		registrationService.createNewRegistration(registration, crsApplicationUser);

		RegistrationEntity foundRegistration = em.find(RegistrationEntity.class, registration.getId());

		Assert.assertNotNull(foundRegistration);
		Assert.assertEquals(foundRegistration.getId(), registration.getId());
		Assert.assertEquals(foundRegistration.getUserId(), registration.getUserId());
		Assert.assertEquals(foundRegistration.getConference().getId(), registration.getConference().getId());

		em.remove(foundRegistration);
	}

	@Test(groups="db-integration-tests")
	public void testUpdateRegistration() throws UnauthorizedException
	{
		RegistrationEntity registration = registrationService.getRegistrationBy(originalRegistrationUUID, crsApplicationUser);

		UUID updatedUserUUID = UUID.randomUUID();

		registration.setUserId(updatedUserUUID);

		registrationService.updateRegistration(registration, adminCrsApplicationUser);

		RegistrationEntity updatedRegistration = em.find(RegistrationEntity.class, originalRegistrationUUID);

		Assert.assertEquals(updatedRegistration.getUserId(), updatedUserUUID);

		updatedRegistration.setUserId(originalUserUUID);

		em.merge(updatedRegistration);
    }

	@Test(groups="db-integration-tests")
	public void testDeleteRegistration() throws UnauthorizedException
	{
		RegistrationEntity registration = new RegistrationEntity();

        ConferenceEntity conference = conferenceService.fetchConferenceBy(originalConferenceUUID);

		registration.setId(UUID.randomUUID());
        registration.setConference(conference);
		registration.setUserId(originalUserUUID);

		em.persist(registration);

		registrationService.deleteRegistration(registration, adminCrsApplicationUser);

		Assert.assertNull(em.find(RegistrationEntity.class, registration.getId()));
	}

    @Test(groups="db-integration-tests")
    public void testCreateNewAnswer() throws UnauthorizedException
	{
        AnswerEntity answer = new AnswerEntity();

        RegistrationEntity registrationEntity = registrationService.getRegistrationBy(originalRegistrationUUID, crsApplicationUser);

        answer.setId(UUID.randomUUID());
        answer.setAnswer(originalAnswerValue);

        registrationEntity.getAnswers().add(answer);

        registrationService.updateRegistration(registrationEntity, crsApplicationUser);

        AnswerEntity foundAnswer = em.find(AnswerEntity.class, answer.getId());

        Assert.assertNotNull(foundAnswer);
        Assert.assertEquals(foundAnswer.getId(), answer.getId());
        Assert.assertEquals(foundAnswer.getAnswer(), answer.getAnswer());

        em.remove(foundAnswer);
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