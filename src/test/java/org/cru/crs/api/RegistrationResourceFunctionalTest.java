package org.cru.crs.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.client.AnswerResourceClient;
import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.api.client.RegistrationResourceClient;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.model.errors.BadRequest;
import org.cru.crs.api.model.errors.NotFound;
import org.cru.crs.api.model.errors.Unauthorized;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.service.PaymentService;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.sql2o.QuirksMode;
import org.sql2o.Sql2o;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups="functional-tests")
public class RegistrationResourceFunctionalTest
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;
	ConferenceResourceClient conferenceClient;
	RegistrationResourceClient registrationClient;
	AnswerResourceClient answerClient;

	PaymentService paymentService;
	
	private UUID registrationUUID = UUID.fromString("A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7");
	private UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");
	private UUID paymentUUID = UUID.fromString("8492F4A8-C7DC-4C0A-BB9E-67E6DCB91957");
	
	@BeforeMethod
	public void createClient()
	{
        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        answerClient = ProxyFactory.create(AnswerResourceClient.class, restApiBaseUrl);
        registrationClient = ProxyFactory.create(RegistrationResourceClient.class, restApiBaseUrl);
		conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
		
		paymentService = new PaymentService(new Sql2o("jdbc:postgresql://localhost/crsdb", "crsuser", "crsuser",QuirksMode.PostgreSQL));
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
		ClientResponse<Registration> response = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);

		Registration registration = response.getEntity();

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), registrationUUID);
		Assert.assertEquals(registration.getUserId(), UserInfo.Id.TestUser);
		Assert.assertEquals(registration.getConferenceId(), conferenceUUID);
		Assert.assertNotNull(registration.getCurrentPayment());
		
		Payment payment = registration.getCurrentPayment();
		
		Assert.assertEquals(payment.getId(), paymentUUID);
		Assert.assertEquals(payment.getAmount(), new BigDecimal(50f));
		Assert.assertEquals(payment.getCreditCardNameOnCard(),"Joe User");
		Assert.assertEquals(payment.getCreditCardExpirationMonth(), "11");
		Assert.assertEquals(payment.getCreditCardExpirationYear(), "2015");
		Assert.assertEquals(payment.getCreditCardNumber(), "****1111");
		Assert.assertEquals(payment.getRegistrationId(), registrationUUID);
		Assert.assertFalse(payment.isReadyToProcess());
	}
	
	@Test(groups="functional-tests")
	public void getRegistrationNoPayment()
	{
		ClientResponse<Registration> response = registrationClient.getRegistration(UUID.fromString("B2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7"), UserInfo.AuthCode.Email);
		
		Assert.assertEquals(response.getStatus(), 200);

		Registration registration = response.getEntity();

		Assert.assertNotNull(registration);
		
		Assert.assertTrue(registration.getPastPayments().isEmpty());
	}

    @Test(groups="functional-tests")
    public void getRegistrationMultiplePastPayments()
    {
        ClientResponse<Registration> response = registrationClient.getRegistration(UUID.fromString("AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111"), UserInfo.AuthCode.Ryan);

        Assert.assertEquals(response.getStatus(), 200);

        Registration registration = response.getEntity();

        Assert.assertNotNull(registration);

        Assert.assertEquals(registration.getPastPayments().size(), 2);

        Payment payment1 = registration.getPastPayments().get(0);
        Payment payment2 = registration.getPastPayments().get(1);

        Assert.assertEquals(payment1.getId(), UUID.fromString("8492F4A8-C7DC-4C0A-BB9E-67E6DCB91958"));
        Assert.assertEquals(payment1.getAmount(), new BigDecimal(20f));
        Assert.assertEquals(payment1.getCreditCardNameOnCard(),"Billy User");
        Assert.assertEquals(payment1.getCreditCardExpirationMonth(), "04");
        Assert.assertEquals(payment1.getCreditCardExpirationYear(), "2014");
        Assert.assertEquals(payment1.getCreditCardNumber(), "****1111");
        Assert.assertEquals(payment1.getRegistrationId(), UUID.fromString("AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111"));

        Assert.assertEquals(payment2.getId(), UUID.fromString("8492F4A8-C7DC-4C0A-BB9E-67E6DCB91959"));
        Assert.assertEquals(payment2.getAmount(), new BigDecimal(55f));
        Assert.assertEquals(payment2.getCreditCardNameOnCard(),"Billy User");
        Assert.assertEquals(payment2.getCreditCardExpirationMonth(), "04");
        Assert.assertEquals(payment2.getCreditCardExpirationYear(), "2014");
        Assert.assertEquals(payment2.getCreditCardNumber(), "****1111");
        Assert.assertEquals(payment2.getRegistrationId(), UUID.fromString("AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111"));
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
		ClientResponse response = registrationClient.getRegistration(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0dddd"), UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);
		
		NotFound notFoundError = (NotFound)response.getEntity(NotFound.class);
		Assert.assertEquals(notFoundError.getStatusCode(), 404);
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
		ClientResponse<Registration> response = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
		Registration registration = response.getEntity();
        UUID originalUserId = registration.getUserId();

		// update registration
		UUID updatedUserUUID = UserInfo.Id.Ryan;
		Assert.assertNotEquals(originalUserId, updatedUserUUID);

		registration.setUserId(updatedUserUUID);
		response = registrationClient.updateRegistration(registration, registrationUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 204);

		// get updated registration
		response = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertEquals(response.getEntity().getUserId(), updatedUserUUID);

		// restore registration
		registration.setId(registrationUUID);
        registration.setUserId(originalUserId);
		response = registrationClient.updateRegistration(registration, registrationUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 204);
	}
	
	@Test(groups="functional-tests")
	public void updateRegistrationProcessPayment()
	{
		ClientResponse<Registration> response = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
		
		PaymentEntity processedPayment = null;
		
		Assert.assertEquals(response.getStatus(), 200);

		Registration registration = response.getEntity();
		registration.getCurrentPayment().setCreditCardNumber("4111111111111111");
		registration.getCurrentPayment().setCreditCardCVVNumber("822");
		registration.getCurrentPayment().setReadyToProcess(true);
		
		try
		{

			ClientResponse updateResponse = registrationClient.updateRegistration(registration, registration.getId(), UserInfo.AuthCode.TestUser);

			Assert.assertEquals(updateResponse.getStatus(), 204);

			processedPayment = paymentService.fetchPaymentBy(registration.getCurrentPayment().getId());

			Assert.assertNotNull(processedPayment.getAuthnetTransactionId());
			Assert.assertNotNull(processedPayment.getTransactionTimestamp());

			ClientResponse<Registration> subsequentResponse = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.TestUser);
			Registration regstrationAfterPayment = subsequentResponse.getEntity();

			Assert.assertNull(regstrationAfterPayment.getCurrentPayment());
			Assert.assertEquals(regstrationAfterPayment.getPastPayments().size(), 1);
			Assert.assertEquals(regstrationAfterPayment.getPastPayments().get(0).getId(), processedPayment.getId());
		}
		finally
		{
			if(processedPayment != null)
			{
				processedPayment.setAuthnetTransactionId(null);
				processedPayment.setTransactionTimestamp(null);
				paymentService.updatePayment(processedPayment);
			}
		}

	}

	@Test(groups="functional-tests")
	public void createRegistrationOnUpdate() throws URISyntaxException
	{
		UUID registrationIdUUID = UUID.randomUUID();
		UUID userIdUUID = UserInfo.Id.Ryan;
		UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

		Registration createRegistration = createRegistration(registrationIdUUID, userIdUUID, conferenceUUID);

		// create registration through update
		ClientResponse<Registration> response = registrationClient.updateRegistration(createRegistration, registrationIdUUID, UserInfo.AuthCode.Ryan);
		Assert.assertEquals(response.getStatus(), 201);

		Registration registration = response.getEntity();

		Assert.assertEquals(registration.getConferenceId(), createRegistration.getConferenceId());
		Assert.assertEquals(registration.getId(), createRegistration.getId());
		Assert.assertEquals(registration.getUserId(), createRegistration.getUserId());

		// get updated registration
		response = registrationClient.getRegistration(registrationIdUUID, UserInfo.AuthCode.Ryan);
		Assert.assertEquals(response.getStatus(), 200);

		registration = response.getEntity();
		Assert.assertEquals(registration.getConferenceId(), createRegistration.getConferenceId());
		Assert.assertEquals(registration.getId(), createRegistration.getId());
		Assert.assertEquals(registration.getUserId(), createRegistration.getUserId());

		// delete created registration
		response = registrationClient.deleteRegistration(registrationIdUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 204);
	}

	@Test(groups="functional-tests")
	public void createRegistrationOnUpdateUserAlreadyRegistered() throws URISyntaxException
	{
		UUID registrationIdUUID = UUID.randomUUID();
		UUID userIdUUID = UserInfo.Id.TestUser;
		UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

		Registration createRegistration = createRegistration(registrationIdUUID, userIdUUID, conferenceUUID);

		// create registration through update
		ClientResponse response = registrationClient.updateRegistration(createRegistration, registrationIdUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 200);
		
		Unauthorized unauthorizedError = (Unauthorized) response.getEntity(Unauthorized.class);
		Assert.assertEquals(unauthorizedError.getStatusCode(), 401);
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
		Registration registration = createRegistration(randomRegistrationUUID, UserInfo.Id.Ryan, conferenceUUID);

		ClientResponse response = registrationClient.updateRegistration(registration, UUID.fromString("0a00d62c-af29-3723-f949-95a950a0eeee"), UserInfo.AuthCode.TestUser);

		Assert.assertEquals(response.getStatus(), 200);
		
		BadRequest badRequestError = (BadRequest) response.getEntity(BadRequest.class);
		Assert.assertEquals(badRequestError.getStatusCode(), 400);
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
        ClientResponse<Registration> response = null;

		// create registration
		UUID createUserUUID = UserInfo.Id.Email;
        UUID createRegistrationIdUUID = UUID.randomUUID();
		Registration registration = createRegistration(createRegistrationIdUUID, createUserUUID, conferenceUUID);

		response = conferenceClient.createRegistration(registration, conferenceUUID, UserInfo.AuthCode.Ryan);
        Assert.assertEquals(response.getStatus(), 201);

        // delete registration
		response = registrationClient.deleteRegistration(createRegistrationIdUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 204);
	}

	@Test(groups="functional-tests")
	public void createAnswer()
	{
		// create answer
		UUID createBlockUUID = UUID.fromString("AF60D878-4741-4F21-9D25-231DB86E43EE");
		JsonNode createAnswerValue = jsonNodeFromString("{\"Name\": \"Alex Solz\"}");
		Answer answer = createAnswer(null, registrationUUID, createBlockUUID, createAnswerValue);
		
		ClientResponse<Answer> registrationResponse = registrationClient.createAnswer(answer, registrationUUID, UserInfo.AuthCode.TestUser);

        Assert.assertEquals(registrationResponse.getStatus(), 201);
		Answer gotAnswer = registrationResponse.getEntity();
		Assert.assertNotNull(gotAnswer);
		Assert.assertEquals(gotAnswer.getBlockId(), createBlockUUID);
		Assert.assertEquals(gotAnswer.getValue(), createAnswerValue);

        UUID answerIdUUID = getIdFromResponseLocation(registrationResponse.getLocation().toString());

        // get answer

		ClientResponse<Answer> response = answerClient.getAnswer(answerIdUUID, UserInfo.AuthCode.TestUser);

		gotAnswer = response.getEntity();
		Assert.assertEquals(response.getStatus(), 200);

		Assert.assertNotNull(gotAnswer);
		Assert.assertEquals(gotAnswer.getId(), answerIdUUID);
		Assert.assertEquals(gotAnswer.getBlockId(), createBlockUUID);
		Assert.assertEquals(gotAnswer.getValue(), createAnswerValue);

		answer.setId(answerIdUUID);
		response = answerClient.deleteAnswer(answerIdUUID, UserInfo.AuthCode.TestUser);
		Assert.assertEquals(response.getStatus(), 204);
	}

	private UUID getIdFromResponseLocation(String location)
	{
		location = location.substring(1, location.length()-1);

		return UUID.fromString(location.substring(location.lastIndexOf("/")).substring(1));
	}

	private Registration createRegistration(UUID registrationUUID, UUID userUUID, UUID conferenceUUID)
	{
		Registration registration = new Registration();

		registration.setId(registrationUUID);
		registration.setUserId(userUUID);
		registration.setConferenceId(conferenceUUID);

        registration.setAnswers(new HashSet<Answer>());

		return registration;
	}

	private Answer createAnswer(UUID answerUUID, UUID registrationUUID, UUID blockUUID, JsonNode value)
	{
		Answer answer = new Answer();

		answer.setId(answerUUID);
		answer.setRegistrationId(registrationUUID);
		answer.setBlockId(blockUUID);
		answer.setValue(value);

		return answer;
	}

	@Test(groups="functional-tests")
	public void getRegistrationWithExpiredSession()
	{
		ClientResponse<Registration> response = registrationClient.getRegistration(registrationUUID, UserInfo.AuthCode.Expired);

		Assert.assertEquals(response.getStatus(), 200);
		
		Unauthorized unauthorizedError = (Unauthorized) response.getEntity(Unauthorized.class);
		Assert.assertEquals(unauthorizedError.getStatusCode(), 401);
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