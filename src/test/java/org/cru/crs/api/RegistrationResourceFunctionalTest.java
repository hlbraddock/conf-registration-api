package org.cru.crs.api;

import org.cru.crs.api.client.AnswerResourceClient;
import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.api.client.RegistrationResourceClient;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.utils.Environment;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.net.URISyntaxException;
import java.util.UUID;

@Test(groups="functional-tests")
public class RegistrationResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsLocalTest";
	
	Environment environment = Environment.LOCAL;
	ConferenceResourceClient conferenceClient;
	RegistrationResourceClient registrationClient;
	AnswerResourceClient answerClient;

	private UUID registrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");
	private UUID userUUID = UUID.fromString("1F6250CA-6D25-2BF4-4E56-F368B2FB8F8A");
	private UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        registrationClient = ProxyFactory.create(RegistrationResourceClient.class, restApiBaseUrl);
		conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
	}
	
	/**
	 * Test: find a registration specified by ID
	 * 
	 * Expected outcome: registration is found.
	 * 
	 * Expected return: 200 - OK and Registration JSON resource specified by input ID.
	 */
	@Test(groups="functional-tests")
	public void getRegistration()
	{
		ClientResponse<Registration> response = registrationClient.getRegistration(registrationUUID);
		
		Assert.assertEquals(response.getStatus(), 200);
		
		Registration registration = response.getEntity();

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), registrationUUID);
		Assert.assertEquals(registration.getUserId(), userUUID);
	}
	
	/**
	 * Test: test endpoint with id that does not exist
	 * 
	 * Expected outcome: registration is not found.
	 * 
	 * Expected return: 404 - NOT FOUND
	 */
	@Test(groups="functional-tests")
	public void getRegistrationNotFound()
	{
		ClientResponse<Registration> response = registrationClient.getRegistration(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
		
		Assert.assertEquals(response.getStatus(), 404);
	}
	
	/**
	 * Test: test update endpoint with a valid registration ID (path and body IDs match), by changing the name of the registration
	 * 
	 * Expected outcome: registration receives updated name
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups = "functional-tests")
	public void updateRegistration()
	{
		//get registration
		ClientResponse<Registration> response = registrationClient.getRegistration(registrationUUID);
		Registration registration = response.getEntity();

		// update registration
		UUID updatedUserUUID = UUID.randomUUID();
		registration.setUserId(updatedUserUUID);
		response = registrationClient.updateRegistration(registration, registrationUUID);
		Assert.assertEquals(response.getStatus(), 204);

		// get updated registration
		response = registrationClient.getRegistration(registrationUUID);
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertEquals(response.getEntity().getUserId(), updatedUserUUID);

		// restore registration
		registration.setId(registrationUUID);
		response = registrationClient.updateRegistration(registration, registrationUUID);
		Assert.assertEquals(response.getStatus(), 204);
	}

	/**
	 * Test: test update endpoint where the registration ID specified in the path does not match the registration ID
	 * in the body of the Registration JSON resource
	 * 
	 * Expected outcome: update should fail since there is a mismatch in IDs
	 * 
	 * Input: JSON registration resource with registration ID that doesn't match path registration ID
	 * 
	 * Expected output: 400 - BAD REQUEST
	 */
	@Test(groups="functional-tests")
	public void updateRegistrationWherePathAndBodyRegistrationIdsDontMatch()
	{
		UUID randomRegistrationUUID = UUID.fromString("0a00d62c-af29-3723-f949-95a950a0face");
		UUID randomUserUUID = UUID.fromString("0a00d62c-af29-3723-f949-95a950a0fade");
		Registration registration = createRegistration(randomRegistrationUUID, randomUserUUID);

		ClientResponse<Registration> response = registrationClient.updateRegistration(registration, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0eeee"));

		Assert.assertEquals(response.getStatus(), 400);
	}

	/**
	 * Test: test delete registration endpoint
	 * 
	 * Expected outcome: registration resource specified by ID:  .. should be deleted
	 * 
	 * Input: JSON registration resource with registration ID: 
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups="functional-tests")
	public void deleteRegistration()
	{
		// create registration
		UUID createUserUUID = UUID.fromString("0a00d62c-af29-3723-f949-95a950a5678");
		Registration registration = createRegistration(null, createUserUUID);
		ClientResponse<Registration> response = conferenceClient.createRegistration(registration, conferenceUUID);
		UUID createdRegistrationUUID = response.getEntity().getId();

		// get registration
		response = registrationClient.getRegistration(createdRegistrationUUID);
		Assert.assertEquals(response.getStatus(), 200);

		// delete registration
		response = registrationClient.deleteRegistration(registration, createdRegistrationUUID);
		Assert.assertEquals(response.getStatus(), 200);
	}

	@Test(groups="functional-tests")
	public void createAnswer()
	{
		// create answer
		UUID createBlockUUID = UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EE");
		String createAnswerValue = "{ \"Name\": \"Alex Solz\"}";
		Answer answer = createAnswer(null, createBlockUUID, createAnswerValue);
		ClientResponse<Answer> response = registrationClient.createAnswer(answer, registrationUUID);
		UUID createdAnswerUUID = response.getEntity().getId();

		// get answer
		response = answerClient.getAnswer(createdAnswerUUID);
		Answer gotAnswer = response.getEntity();
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertNotNull(gotAnswer);
		Assert.assertEquals(gotAnswer.getId(), createdAnswerUUID);
		Assert.assertEquals(gotAnswer.getBlockId(), createBlockUUID);
		Assert.assertEquals(gotAnswer.getValue(), createAnswerValue);
	}

	private Registration createRegistration(UUID registrationUUID, UUID userUUID)
	{
		Registration registration = new Registration();

		registration.setId(registrationUUID);
		registration.setUserId(userUUID);

		return registration;
	}

	private Answer createAnswer(UUID answerUUID, UUID blockUUID, String value)
	{
		Answer answer = new Answer();

		answer.setId(answerUUID);
		answer.setBlockId(blockUUID);
		answer.setValue(value);

		return answer;
	}
}