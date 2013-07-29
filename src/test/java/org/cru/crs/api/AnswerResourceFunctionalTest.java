package org.cru.crs.api;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.api.client.AnswerResourceClient;
import org.cru.crs.api.client.RegistrationResourceClient;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Registration;
import org.cru.crs.utils.Environment;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

@Test(groups="functional-tests")
public class AnswerResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;
	RegistrationResourceClient registrationClient;
	AnswerResourceClient answerClient;

	UUID registrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");

	UUID answerUUID = UUID.fromString("441AD805-7AA6-4B20-8315-8F1390DC4A9E");
	UUID blockUUID = UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EE");
	JsonNode answerValue = jsonNodeFromString("{\"Imya\":\"Alexander Solzhenitsyn\"}");

	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        answerClient = ProxyFactory.create(AnswerResourceClient.class, restApiBaseUrl);
		registrationClient = ProxyFactory.create(RegistrationResourceClient.class, restApiBaseUrl);
	}

	/**
	 * Test: find a answer specified by ID
	 * 
	 * Expected outcome: answer is found.
	 * 
	 * Expected return: 200 - OK and Answer JSON resource specified by input ID.
	 */
	@Test(groups="functional-tests")
	public void getAnswer()
	{
		ClientResponse<Answer> response = answerClient.getAnswer(answerUUID);

		Assert.assertEquals(response.getStatus(), 200);

		Answer answer = response.getEntity();

		Assert.assertNotNull(answer);
		Assert.assertEquals(answer.getId(), answerUUID);
		Assert.assertEquals(answer.getBlockId(), blockUUID);
		Assert.assertEquals(answer.getValue(), answerValue);
	}
	
	/**
	 * Test: test endpoint with id that does not exist
	 * 
	 * Expected outcome: answer is not found.
	 * 
	 * Expected return: 404 - NOT FOUND
	 */
	@Test(groups="functional-tests")
	public void getAnswerNotFound()
	{
		ClientResponse<Answer> response = answerClient.getAnswer(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"));
		
		Assert.assertEquals(response.getStatus(), 404);
	}
	
	/**
	 * Test: test update endpoint with a valid answer ID (path and body IDs match), by changing the name of the answer
	 * 
	 * Expected outcome: answer receives updated name
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups = "functional-tests")
	public void updateAnswer()
	{
		// get answer
		ClientResponse<Answer> response = answerClient.getAnswer(answerUUID);
		Answer answer = response.getEntity();

		// update answer
		JsonNode updatedAnswerValue = jsonNodeFromString("{\"Nombre\": \"Alexandrea Solzendo\"}");
		answer.setValue(updatedAnswerValue);
		response = answerClient.updateAnswer(answer, answerUUID);
		Assert.assertEquals(response.getStatus(), 204);

		// get updated answer
		response = answerClient.getAnswer(answerUUID);
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertEquals(response.getEntity().getValue(), updatedAnswerValue);

		// restore answer
		answer.setValue(answerValue);
		response = answerClient.updateAnswer(answer, answerUUID);
		Assert.assertEquals(response.getStatus(), 204);
	}

	/**
	 * Test: test update endpoint where the answer ID specified in the path does not match the answer ID
	 * in the body of the Answer JSON resource
	 * 
	 * Expected outcome: update should fail since there is a mismatch in IDs
	 * 
	 * Input: JSON answer resource with answer ID that doesn't match path answer ID
	 * 
	 * Expected output: 400 - BAD REQUEST
	 */
	@Test(groups="functional-tests")
	public void updateAnswerWherePathAndBodyAnswerIdsDontMatch()
	{
		UUID randomAnswerUUID = UUID.fromString("0a00d62c-af29-3723-f949-95a950a09876");
		UUID randomBlockUUID = UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EE");
		JsonNode randomAnswerValue = jsonNodeFromString("{ \"N\": \"Oleg Salvador\"}");
		Answer answer = createAnswer(randomAnswerUUID, randomBlockUUID, randomAnswerValue);

		ClientResponse<Answer> response = answerClient.updateAnswer(answer, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cade"));

		Assert.assertEquals(response.getStatus(), 400);
	}

	/**
	 * Test: test delete answer endpoint
	 * 
	 * Expected outcome: answer resource specified by ID:  .. should be deleted
	 * 
	 * Input: JSON answer resource with answer ID: 
	 * 
	 * Expected output: 204 - NO CONTENT
	 */
	@Test(groups="functional-tests")
	public void deleteAnswer()
	{
		ClientResponse<Answer> response;

		// create answer
		UUID createBlockUUID = UUID.fromString("AF60D878-4741-4F21-9D25-231DB86Ebaba");
		UUID createAnswerIdUUID = UUID.randomUUID();
		JsonNode createAnswerValue = jsonNodeFromString("{\"N\": \"Oleg Salvador\"}");
		Answer answer = createAnswer(createAnswerIdUUID, createBlockUUID, createAnswerValue);

		response = registrationClient.createAnswer(answer, registrationUUID);
		Assert.assertEquals(response.getStatus(), 201);

		// get answer
//		response = answerClient.getAnswer(createdAnswerUUID);
//		Assert.assertEquals(response.getStatus(), 200);

		// delete answer
		response = answerClient.deleteAnswer(answer, createAnswerIdUUID);
		Assert.assertEquals(response.getStatus(), 200);
	}

	private Answer createAnswer(UUID answerUUID, UUID blockUUID, JsonNode value)
	{
		Answer answer = new Answer();

		answer.setId(answerUUID);
		answer.setBlockId(blockUUID);
		answer.setValue(value);

		return answer;
	}

	private static JsonNode jsonNodeFromString(String jsonString)
	{
		try
		{
			return (new ObjectMapper()).readTree(jsonString);
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}