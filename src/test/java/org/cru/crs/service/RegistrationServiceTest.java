package org.cru.crs.service;

import org.cru.crs.model.RegistrationEntity;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.UUID;

@Test(groups="db-integration-tests")
public class RegistrationServiceTest
{
	private static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	private EntityManagerFactory emFactory;
	private EntityManager em;

	private RegistrationService registrationService;

	private UUID originalRegistrationUUID = UUID.fromString("7a52af36-2f3c-5e45-9f76-0af10ff50bb8");
	private UUID originalUserUUID = UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309");
	private UUID originalConferenceUUID = UUID.fromString("42e4c1b2-0cc1-89f7-9f4b-6bc3e0db5309");

	@BeforeClass
	public void setup()
	{
		emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emFactory.createEntityManager();
		
		registrationService = new RegistrationService(em);
	}

	@AfterClass
	public void cleanup()
	{
		em.close();
		emFactory.close();
	}

	@Test(groups="db-integration-tests")
	public void getRegistrationById()
	{
		RegistrationEntity registration = registrationService.getRegistrationBy(originalRegistrationUUID);

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getUserId(), originalUserUUID);
		Assert.assertEquals(registration.getConferenceId(), originalConferenceUUID);
	}

	@Test(groups="db-integration-tests")
	public void testCreateNewRegistration()
	{
		RegistrationEntity registration = new RegistrationEntity();

		registration.setId(UUID.randomUUID());
		registration.setUserId(originalUserUUID);
		registration.setConferenceId(originalConferenceUUID);

		registrationService.createNewRegistration(registration);

		RegistrationEntity foundRegistration = em.find(RegistrationEntity.class, registration.getId());

		Assert.assertNotNull(foundRegistration);
		Assert.assertEquals(foundRegistration.getId(), registration.getId());
		Assert.assertEquals(foundRegistration.getUserId(), registration.getUserId());
		Assert.assertEquals(foundRegistration.getConferenceId(), registration.getConferenceId());

		em.remove(foundRegistration);
	}

	@Test(groups="db-integration-tests")
	public void testUpdateRegistration()
	{
		RegistrationEntity registration = registrationService.getRegistrationBy(originalRegistrationUUID);

		UUID updatedUserUUID = UUID.randomUUID();

		registration.setUserId(updatedUserUUID);

		registrationService.updateRegistration(registration);

		RegistrationEntity updatedRegistration = em.find(RegistrationEntity.class, originalRegistrationUUID);

		Assert.assertEquals(updatedRegistration.getUserId(), updatedUserUUID);

		updatedRegistration.setUserId(originalUserUUID);

		em.merge(updatedRegistration);
	}

	@Test(groups="db-integration-tests")
	public void testDeleteRegistration()
	{
		RegistrationEntity registration = new RegistrationEntity();

		registration.setId(UUID.randomUUID());
		registration.setUserId(originalUserUUID);
		registration.setConferenceId(originalConferenceUUID);

		em.persist(registration);
		
		registrationService.deleteRegistration(registration);
		
		Assert.assertNull(em.find(RegistrationEntity.class, registration.getId()));
	}
}