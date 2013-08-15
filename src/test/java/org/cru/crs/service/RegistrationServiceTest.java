package org.cru.crs.service;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.auth.AuthenticationProviderType;
import org.cru.crs.auth.CrsApplicationUser;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.authz.AuthorizationService;
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
	private UUID originalUserUUID = UUID.fromString("1F6250CA-6D25-2BF4-4E56-F368B2FB8F8A");
	private UUID originalConferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

	private String originalUserAuthProviderId = "ABC4C1B2-0CC1-89F7-9F4B-6BC3E0DB5123";

	private UUID adminUserUUID = UUID.fromString("f8f8c217-f918-4503-b3b3-85016f9883c1");

	private CrsApplicationUser crsApplicationUser;
	private CrsApplicationUser adminCrsApplicationUser;
    private JsonNode originalAnswerValue = jsonNodeFromString("{ \"Name\": \"Alexander Solzhenitsyn\"}");

    @BeforeMethod
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();

        registrationService = new RegistrationService(em, new AuthorizationService());
        conferenceService = new ConferenceService(em);

		crsApplicationUser = new CrsApplicationUser(originalUserUUID, originalUserAuthProviderId, AuthenticationProviderType.FACEBOOK);
		adminCrsApplicationUser = new CrsApplicationUser(adminUserUUID, originalUserAuthProviderId, AuthenticationProviderType.FACEBOOK);
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

		CrsApplicationUser someCrsApplicationUser = new CrsApplicationUser(someUserUUID, someUserAuthProviderId, AuthenticationProviderType.FACEBOOK);

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