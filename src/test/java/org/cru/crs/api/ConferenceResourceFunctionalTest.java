package org.cru.crs.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import org.cru.crs.AbstractTestWithDatabaseConnectivity;
import org.cru.crs.api.client.ConferenceResourceClient;
import org.cru.crs.api.client.PaymentResourceClient;
import org.cru.crs.api.model.Block;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Permission;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.model.RegistrationView;
import org.cru.crs.api.process.ProfileProcess;
import org.cru.crs.api.process.RetrieveConferenceProcess;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.BlockEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.PaymentType;
import org.cru.crs.model.PermissionEntity;
import org.cru.crs.model.PermissionLevel;
import org.cru.crs.model.ProfileType;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.model.RegistrationViewEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.PermissionService;
import org.cru.crs.service.ProfileService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.ClockImpl;
import org.cru.crs.utils.ConferenceInfo;
import org.cru.crs.utils.DateTimeCreaterHelper;
import org.cru.crs.utils.Environment;
import org.cru.crs.utils.JsonNodeHelper;
import org.cru.crs.utils.ServiceFactory;
import org.cru.crs.utils.UserInfo;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This test requires an EE app server to be running to test the endpoint/resource
 * @author ryancarlson
 */
@Test(groups="functional-tests")
public class ConferenceResourceFunctionalTest extends AbstractTestWithDatabaseConnectivity
{
	static final String RESOURCE_PREFIX = "rest";
	static final String PERSISTENCE_UNIT_NAME = "crsUnitTestPersistence";
	
	Environment environment = Environment.LOCAL;

	ConferenceResourceClient conferenceClient;

	ConferenceService conferenceService;
	PaymentService paymentService;
    PaymentResourceClient paymentClient;
	PageService pageService;
	RegistrationService registrationService;
	PermissionService permissionService;
	AnswerService answerService;
	ProfileService profileService;
	ProfileProcess profileProcess;

	RetrieveConferenceProcess retrieveConferenceProcess;

	@BeforeMethod(alwaysRun = true)
	private void createClient()
	{
		refreshConnection();

        String restApiBaseUrl = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX;
        conferenceClient = ProxyFactory.create(ConferenceResourceClient.class, restApiBaseUrl);
        paymentClient = ProxyFactory.create(PaymentResourceClient.class, restApiBaseUrl);

        answerService = new AnswerService(sqlConnection);
        BlockService blockService = new BlockService(sqlConnection, answerService);
        ConferenceCostsService conferenceCostsService = new ConferenceCostsService(sqlConnection);

        paymentService = new PaymentService(sqlConnection);
        permissionService = new PermissionService(sqlConnection);
        
        conferenceService = ServiceFactory.createConferenceService(sqlConnection);
        pageService = ServiceFactory.createPageService(sqlConnection);
        registrationService = ServiceFactory.createRegistrationService(sqlConnection);

		profileService = new ProfileService(sqlConnection, new ClockImpl());
		profileProcess = new ProfileProcess(blockService, profileService, pageService, ServiceFactory.createUserService(sqlConnection));

		answerService = new AnswerService(sqlConnection);

        retrieveConferenceProcess = new RetrieveConferenceProcess(conferenceService, conferenceCostsService, pageService, blockService, registrationService, new ClockImpl());
	}
	
	/**
	 * Test: find all the conferences in the system
	 * 
	 * Expected outcome: 0 conferences found
	 * 
	 * Input: none
	 * 
	 * Expected return: 200 - OK and empty array of json-i-fied conferences, b/c this list is now limited
	 * by the authenticated user in the session
	 */
	@Test(groups="functional-tests")
	public void getAllTheConferences()
	{
		ClientResponse<List<Conference>> response = conferenceClient.getConferences(UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);
		List<Conference> conferences = response.getEntity();
		
		Assert.assertNotNull(conferences);
		Assert.assertEquals(conferences.size(), 3);
		
		for(Conference conference : conferences)
		{
			if(conference.getId().equals(ConferenceInfo.Id.NorthernMichigan))
			{
				Assert.assertEquals(conference.getRegistrationCount().intValue(), 2);
				Assert.assertEquals(conference.getCompletedRegistrationCount().intValue(), 0);
			}

			if(!("Northern Michigan Fall Extravaganza".equals(conference.getName()) ||
					"Miami University Fall Retreat".equals(conference.getName()) ||
						"Winter Beach Weekend!".equals(conference.getName())))
			{
				Assert.fail();
			}
		}
	}
	
	/**
	 * Test: find conference by id
	 * 
	 * Expected outcome: conference found and returned as json w/ associated pages and blocks
	 * 
	 * Input: UUID - d5878eba-9b3f-7f33-8355-3193bf4fb698
	 * 
	 * Expected return: 200 - OK and conference JSON resource specified by ID
	 */
	@Test(groups="functional-tests")
	public void getConferenceById()
	{
		ClientResponse<Conference> response = conferenceClient.getConference(ConferenceInfo.Id.NewYork);
		
		Assert.assertEquals(response.getStatus(), 200);
		Conference conference = response.getEntity();
		
		Assert.assertNotNull(conference);
		Assert.assertEquals(conference.getName(), "New York U. Retreat Weekend");
		Assert.assertEquals(conference.getTotalSlots(), 116);
	}


	/**
	 * Test: create a new conference
	 * 
	 * Expected outcome: conference created and location header of new conference is returned
	 * 
	 * Input: conference JSON resource
	 * 
	 * Expected return: 201 - Created and location header of new conference resource
	 */
	@Test(groups="functional-tests")
	public void createConference() throws URISyntaxException
	{
		Conference fakeConference = createFakeConference();
		String conferenceIdString = null;
		UUID permissionId = null;
		
		try
		{
			ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";
			Conference conferenceReturnedFromCreateCall = response.getEntity();

			/*status should be 201 - CREATED*/
			Assert.assertEquals(response.getStatus(), 201);

			/*make sure we get a copy of the conference back*/
			Assert.assertNotNull(conferenceReturnedFromCreateCall);

			/*make sure the returned location has the resource path we expect*/
			Assert.assertTrue(returnedLocationHeader.contains(resourceFullPathWithoutId));

			/*parse out the conference id from the returned header using the full resource path we expect*/
			conferenceIdString = returnedLocationHeader.substring(resourceFullPathWithoutId.length());

			Assert.assertTrue(conferenceReturnedFromCreateCall.getId().toString().equals(conferenceIdString));

			/*update our fake conference with the Id generated by the server*/
			fakeConference.setId(UUID.fromString(conferenceIdString));
			
			/*There should be a permission row saved for this user*/
			PermissionEntity newPermission = permissionService.getPermissionForUserOnConference(UserInfo.Id.TestUser, conferenceReturnedFromCreateCall.getId());
			Assert.assertNotNull(newPermission);
			
			permissionId = newPermission.getId();

			Assert.assertEquals(newPermission.getPermissionLevel(), PermissionLevel.CREATOR);
			Assert.assertEquals(newPermission.getEmailAddress(), UserInfo.Email.TestUser);

		}
		finally
		{
			if(conferenceIdString != null) deleteConferenceForNextTests(UUID.fromString(conferenceIdString));
			
			if(permissionId != null)
			{
				sqlConnection.createQuery("DELETE FROM permissions WHERE id = :id")
								.addParameter("id", permissionId)
								.executeUpdate();
			}
			sqlConnection.commit();
		}
	}

	
	/**
	 * Test: update existing conference by changing name
	 * 
	 * Expected outcome: conference after update can be retrieved and new name is verified
	 * 
	 * Input: Updated conference object
	 * 
	 * Expected return: 204 - No Content
	 */
	@Test(groups="functional-tests")
	public void updateConference() throws URISyntaxException
	{
		Conference fakeConference = createFakeConference();
		UUID conferenceId = null;

		try
		{
			ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";

			/*parse out the conference id from the returned header using the full resource path we expect*/
			conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			/*update our fake conference with the Id generated by the server*/
			fakeConference.setId(conferenceId);

			/*update name value on conference*/
			fakeConference.setName("Updated Fake Fall Retreat");

			/*get a fresh client*/
			createClient();

			/*call the update endpoint*/
			@SuppressWarnings("rawtypes")
			ClientResponse updateResponse = conferenceClient.updateConference(fakeConference, conferenceId, UserInfo.AuthCode.TestUser);
			Assert.assertEquals(updateResponse.getStatus(), 204);

			/*get a fresh client*/
			createClient();

			ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
			Conference updatedFakeConference = fetchResponse.getEntity();

			Assert.assertEquals(updatedFakeConference.getName(), "Updated Fake Fall Retreat");
		}
		finally
		{
			deleteConferenceForNextTests(conferenceId);
			sqlConnection.commit();
		}
	}




    /**
     * Test: update existing conference by archiving it
     *
     * Expected outcome: conference after update can be retrieved and is archived
     *
     * Input: Updated conference object
     *
     * Expected return: 204 - No Content
     */
    @Test(groups="functional-tests")
    public void archiveConference() throws URISyntaxException
    {
        Conference fakeConference = createFakeConference();
        UUID conferenceId = null;

        try
        {
            ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

            String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
            String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";

			/*parse out the conference id from the returned header using the full resource path we expect*/
            conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			/*update our fake conference with the Id generated by the server*/
            fakeConference.setId(conferenceId);

			/*update name value on conference*/
            fakeConference.setArchived(true);

			/*get a fresh client*/
            createClient();

			/*call the update endpoint*/
            @SuppressWarnings("rawtypes")
            ClientResponse updateResponse = conferenceClient.updateConference(fakeConference, conferenceId, UserInfo.AuthCode.TestUser);
            Assert.assertEquals(updateResponse.getStatus(), 204);

			/*get a fresh client*/
            createClient();

            ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
            Conference updatedFakeConference = fetchResponse.getEntity();

            Assert.assertTrue(updatedFakeConference.isArchived());
        }
        finally
        {
            deleteConferenceForNextTests(conferenceId);
            sqlConnection.commit();
        }
    }

    /**
     * Test: Try to archive a conference without having adequate permissions
     *
     * Expected outcome: conference after update can be retrieved and isn't changed
     *
     * Input: Updated conference object
     *
     * Expected return: 401 - Unauthorized
     */
    @Test(groups="functional-tests")
    public void archiveConferenceWithoutPermissions() throws URISyntaxException
    {
        Conference fakeConference = createFakeConference();
        UUID conferenceId = null;

        try
        {
            ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

            String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
            String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";

			/*parse out the conference id from the returned header using the full resource path we expect*/
            conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			/*update our fake conference with the Id generated by the server*/
            fakeConference.setId(conferenceId);

			/*update name value on conference*/
            fakeConference.setArchived(true);

			/*get a fresh client*/
            createClient();

			/*call the update endpoint with anonymous user*/
            @SuppressWarnings("rawtypes")
            ClientResponse updateResponse = conferenceClient.updateConference(fakeConference, conferenceId, UserInfo.AuthCode.Anonymous);
            Assert.assertEquals(updateResponse.getStatus(), 401);

			/*get a fresh client*/
            createClient();

            ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
            Conference updatedFakeConference = fetchResponse.getEntity();

            Assert.assertFalse(updatedFakeConference.isArchived());
        }
        finally
        {
            deleteConferenceForNextTests(conferenceId);
            sqlConnection.commit();
        }
    }

    /**
     * Test: delete existing conference
     *
     * Expected outcome: conference doesn't exist after deletion
     *
     * Input:
     *
     * Expected return: 204 - No Content
     */
    @Test(groups="functional-tests")
    public void deleteConference() throws URISyntaxException
    {
        Conference fakeConference = createFakeConference();
        UUID conferenceId = null;

        try
        {
            ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

            String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
            String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";

			/*parse out the conference id from the returned header using the full resource path we expect*/
            conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			/*update our fake conference with the Id generated by the server*/
            fakeConference.setId(conferenceId);

			/*get a fresh client*/
            createClient();

			/*call the delete endpoint*/
            @SuppressWarnings("rawtypes")
            ClientResponse updateResponse = conferenceClient.deleteConference(conferenceId, UserInfo.AuthCode.TestUser);
            Assert.assertEquals(updateResponse.getStatus(), 204);

			/*get a fresh client*/
            createClient();

            // attempt to fetch the conference we just deleted
            ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
            Assert.assertEquals(fetchResponse.getStatus(), 404);
        }
        finally
        {
            deleteConferenceForNextTests(conferenceId);
            sqlConnection.commit();
        }
    }

    /**
     * Test: Try to delete an existing conference without adequate permissions
     *
     * Expected outcome: conference does exist after deletion
     *
     * Input:
     *
     * Expected return: 401 - Unauthorized
     */
    @Test(groups="functional-tests")
    public void deleteConferenceWithoutPermissions() throws URISyntaxException
    {
        Conference fakeConference = createFakeConference();
        UUID conferenceId = null;

        try
        {
            ClientResponse<Conference> response = conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

            String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
            String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/conferences/";

			/*parse out the conference id from the returned header using the full resource path we expect*/
            conferenceId = UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			/*update our fake conference with the Id generated by the server*/
            fakeConference.setId(conferenceId);

			/*get a fresh client*/
            createClient();

			/*call the delete endpoint with anonymous user*/
            @SuppressWarnings("rawtypes")
            ClientResponse updateResponse = conferenceClient.deleteConference(conferenceId, UserInfo.AuthCode.Anonymous);
            Assert.assertEquals(updateResponse.getStatus(), 401);

			/*get a fresh client*/
            createClient();

            // fetch the conference we just attempted to delete
            ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
            Assert.assertEquals(fetchResponse.getStatus(), 200);
        }
        finally
        {
            deleteConferenceForNextTests(conferenceId);
            sqlConnection.commit();
        }
    }

    /**
     * Test: Attempt to delete a conference with registrations attached to it.
     *
     * Expected outcome: conference does exist after deletion
     *
     * Input:
     *
     * Expected return: 401 - Unauthorized
     */
    @Test(groups="functional-tests")
    public void deleteConferenceWithRegistrations() throws URISyntaxException
    {
        UUID conferenceId = null;
        UUID registrationId = null;

        try
        {
            Registration newRegistration = createRegistration(registrationId, UserInfo.Id.Ryan);
            ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.Ryan);
            Assert.assertEquals(response.getStatus(), 201);
            String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
            String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/registrations/";
            registrationId =  UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

            conferenceId = ConferenceInfo.Id.NorthernMichigan;

			/*get a fresh client*/
            createClient();

			/*call the delete endpoint*/
            @SuppressWarnings("rawtypes")
            ClientResponse updateResponse = conferenceClient.deleteConference(conferenceId, UserInfo.AuthCode.TestUser);
            Assert.assertEquals(updateResponse.getStatus(), 400);

			/*get a fresh client*/
            createClient();


            // fetch the conference we just attempted to delete
            ClientResponse<Conference> fetchResponse = conferenceClient.getConference(conferenceId);
            Assert.assertEquals(fetchResponse.getStatus(), 200);
        }
        finally
        {
            deleteRegistrationForNextTests(registrationId);
            sqlConnection.commit();
        }
    }

	/**
	 * Test: add a new page to an existing conference
	 * 
	 * Expected outcome: existing conference will have a new page added to it
	 * 
	 * Input: new page JSON resource and conference UUID - d5878eba-9b3f-7f33-8355-3193bf4fb698
	 * 
	 * Expected return: 201 - Created and location header to newly created page
	 */
	@Test(groups="functional-tests")
	public void addPageToConference() throws URISyntaxException 
	{
		Page newPage = createFakePage();
		UUID pageId = null;
		try
		{
			ClientResponse<Page> response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"), UserInfo.AuthCode.Ryan);
			Page pageFromCreatedResponse = response.getEntity();

			//status code, 201-Created
			Assert.assertEquals(response.getStatus(), 201);

			/*make sure we get a copy of the page back*/
			Assert.assertNotNull(pageFromCreatedResponse);
			pageId = pageFromCreatedResponse.getId();
			/*make sure the ID of the created page matches what was passed in*/
			Assert.assertEquals(pageFromCreatedResponse.getId(), newPage.getId());

			List<PageEntity> pages = pageService.fetchPagesForConference(UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb698"));
			Assert.assertEquals(pages.size(), 3);
			Assert.assertEquals(pages.get(2).getId(), UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		}
		finally
		{
			deletePageForNextTests(pageId);
			sqlConnection.commit();
		}
	}

	/**
	 * Test: add a page to conference that doesn't exist
	 * 
	 * Expected outcome: should fail.. cannot add a page to non-existant conference
	 * 
	 * Input: JSON page resource and UUID - d5878eba-9b3f-7f33-8355-3193bf4fb699
	 * 
	 * Expected return: 400 - Bad Request
	 */
	@Test(groups="functional-tests")
	public void addPageToBogusConference() throws URISyntaxException
	{
		Page newPage = createFakePage();

		ClientResponse response = conferenceClient.createPage(newPage, UUID.fromString("d5878eba-9b3f-7f33-8355-3193bf4fb699"), UserInfo.AuthCode.TestUser);

		//status code, 400-Bad Request
		Assert.assertEquals(response.getStatus(), 400);
	}

	@Test(groups="functional-tests")
	public void addRegistrationToConference() throws URISyntaxException
	{
		UUID registrationId = null;
		try
		{
			Registration newRegistration = createRegistration(registrationId, UserInfo.Id.Ryan);

			ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.Ryan);

			Assert.assertEquals(response.getStatus(), 201);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/registrations/";
			registrationId =  UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			RegistrationEntity registration = registrationService.getRegistrationBy(registrationId);
			List<PaymentEntity> payments = paymentService.getPaymentsForRegistration(registrationId);

			Assert.assertEquals(registration.getId(), registrationId);
			Assert.assertEquals(registration.getUserId(), newRegistration.getUserId());
			Assert.assertFalse(registration.getCompleted());
			Assert.assertEquals(payments.size(), 0);
		}
		finally
		{
			deleteRegistrationForNextTests(registrationId);
			sqlConnection.commit();
		}

	}

	@Test(groups="functional-tests")
	public void addRegistrationToConferencePrepopulatedFromProfile() throws URISyntaxException, IOException
	{
		UUID registrationId = null;
		UUID answerId = null;
		try
		{
			Registration newRegistration = createRegistration(registrationId, UserInfo.Id.TestUser);

			ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, ConferenceInfo.Id.WinterBeachCold, UserInfo.AuthCode.TestUser);

			Assert.assertEquals(response.getStatus(), 201);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/registrations/";
			registrationId =  UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			List<AnswerEntity> answerEntities = answerService.getAllAnswersForRegistration(registrationId);

			Set<BlockEntity> blockEntities = profileProcess.fetchBlocksForConference(ConferenceInfo.Id.WinterBeachCold);

			UUID blockId = getBlockIdWithProfileType(blockEntities, ProfileType.EMAIL);

			AnswerEntity answerEntity = getAnswerWithBlockId(answerEntities, blockId);

			answerId = answerEntity.getId();

			Assert.assertNotNull(answerEntity.getAnswer());
			Assert.assertEquals(answerEntity.getAnswer().textValue(), UserInfo.Email.TestUser);
		}
		finally
		{
			deleteAnswerForNextTests(answerId);
			deleteRegistrationForNextTests(registrationId);
			sqlConnection.commit();
		}
	}

	@Test(groups="functional-tests")
	public void addRegistrationToConferenceOnBehalfOfAsAdmin() throws URISyntaxException
	{
		UUID registrationId = UUID.randomUUID();
		try
		{
			Registration newRegistration = new Registration();

			newRegistration.setId(registrationId);
			newRegistration.setCompleted(true);

			ClientResponse<Registration> response = conferenceClient.createRegistrationType(newRegistration, ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.TestUser, "on-behalf-of");

			Assert.assertEquals(response.getStatus(), 201);

			String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
			String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/registrations/";
			registrationId =  UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

			RegistrationEntity registration = registrationService.getRegistrationBy(registrationId);
			List<PaymentEntity> payments = paymentService.getPaymentsForRegistration(registrationId);

			Assert.assertEquals(registration.getId(), registrationId);
			Assert.assertTrue(registration.getCompleted());
			Assert.assertEquals(payments.size(), 0);
		}
		finally
		{
			deleteRegistrationForNextTests(registrationId);
			sqlConnection.commit();
		}
	}

    @Test(groups="functional-tests")
    public void paymentOnBehalfOfAsAdmin() throws URISyntaxException
    {
        UUID registrationId = UUID.randomUUID();
        UUID userIdRyan = UserInfo.Id.Ryan;

        Payment payment = new Payment();
        UUID currentPaymentId = UUID.randomUUID();
        payment.setId(currentPaymentId);
        payment.setRegistrationId(registrationId);
        payment.setPaymentType(PaymentType.CASH);
        payment.setAmount(new BigDecimal("45.00"));
        payment.setReadyToProcess(true);

        try
        {
            Registration newRegistration = new Registration();
            newRegistration.setId(registrationId);
            newRegistration.setUserId(userIdRyan);
            newRegistration.setCompleted(true);

            ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.Ryan);

            Assert.assertEquals(response.getStatus(), 201);
            Assert.assertEquals(registrationId, (response.getEntity()).getId());

            String returnedLocationHeader = response.getHeaderAsLink("Location").getHref();
            String resourceFullPathWithoutId  = environment.getUrlAndContext() + "/" + RESOURCE_PREFIX + "/registrations/";
            registrationId =  UUID.fromString(returnedLocationHeader.substring(resourceFullPathWithoutId.length()));

            RegistrationEntity registration = registrationService.getRegistrationBy(registrationId);
            ClientResponse postResponse = paymentClient.createPayment(payment, UserInfo.AuthCode.TestUser);

            Assert.assertEquals(postResponse.getStatus(), 201);
            List<PaymentEntity> paymentsForRegistration = paymentService.getPaymentsForRegistration(registrationId);
            for(PaymentEntity retrievedPayments : paymentsForRegistration)
            {
                if(currentPaymentId.equals(retrievedPayments.getId()))
                {
                    Assert.assertEquals(payment.getPaymentType(), PaymentType.CASH);
                    Assert.assertEquals(payment.getAmount(), new BigDecimal("45.00"));
                }
            }

            List<PaymentEntity> payments = paymentService.getPaymentsForRegistration(registrationId);
            Assert.assertEquals(registration.getId(), registrationId);
            Assert.assertTrue(registration.getCompleted());
            Assert.assertEquals(payments.size(), 1);
        }
        finally
        {
            //sqlConnection.commit();
            sqlConnection.createQuery("DELETE FROM payments WHERE id = :id")
                    .addParameter("id", currentPaymentId)
                    .executeUpdate();
            deleteRegistrationForNextTests(registrationId);
            sqlConnection.commit();
        }
    }

	@Test(groups="functional-tests")
	public void addRegistrationToConferenceOnBehalfOfNotAsAdmin() throws URISyntaxException
	{
		UUID registrationId = null;
		try
		{
			Registration newRegistration = createRegistration(registrationId, UserInfo.Id.Ryan);

			ClientResponse<Registration> response = conferenceClient.createRegistrationType(newRegistration, ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.Email, "on-behalf-of");

			Assert.assertEquals(response.getStatus(), 401);
		}
		finally
		{
			deleteRegistrationForNextTests(registrationId);
			sqlConnection.commit();
		}
	}

	private AnswerEntity getAnswerWithBlockId(List<AnswerEntity> answerEntities, UUID blockId)
	{
		for(AnswerEntity answerEntity : answerEntities)
		{
			if(answerEntity.getBlockId().equals(blockId))
			{
				return answerEntity;
			}
		}

		throw new RuntimeException("Could not find answer for block id ");
	}

	private UUID getBlockIdWithProfileType(Set<BlockEntity> blockEntities, ProfileType profileType)
	{
		for(BlockEntity blockEntity : blockEntities)
		{
			if(profileProcess.hasProfileType(blockEntity))
			{
				if(blockEntity.getProfileType().equals(profileType))
				{
					return blockEntity.getId();
				}
			}
		}

		throw new RuntimeException("Could not find blcock for block with profile type " + profileType);
	}

	@Test(groups="functional-tests")
	public void createRegistrationUserAlreadyRegistered() throws URISyntaxException
	{
		UUID registrationId = null;
		try
		{
			Registration newRegistration = createRegistration(registrationId, UserInfo.Id.TestUser);

			ClientResponse<Registration> response = conferenceClient.createRegistration(newRegistration, ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.TestUser);

			Assert.assertEquals(response.getStatus(), 401);
		}
		finally
		{
			deleteRegistrationForNextTests(registrationId);
			sqlConnection.commit();
		}


	}

	@Test(groups="functional-tests")
	public void getAllConferenceRegistrations()
	{
		UUID conferenceUUID = UUID.fromString("42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309");

		ClientResponse<List<Registration>> response = conferenceClient.getRegistrations(conferenceUUID, UserInfo.AuthCode.TestUser);

		Assert.assertEquals(response.getStatus(), 200);
		List<Registration> registrations = response.getEntity();

		Set<UUID> userIdUUIDSet = new HashSet<UUID>();

		userIdUUIDSet.add(UserInfo.Id.Email);
		userIdUUIDSet.add(UserInfo.Id.TestUser);

		Assert.assertNotNull(registrations);
		Assert.assertEquals(registrations.size(), 2);

		for(Registration registration : registrations)
		{
			userIdUUIDSet.remove(registration.getUserId());
		}

		Assert.assertTrue(userIdUUIDSet.size() == 0);
	}

	@Test(groups="functional-tests")
	public void getCurrentRegistration()
	{
		UUID conferenceUUID = UUID.fromString("1951613E-A253-1AF8-6BC4-C9F1D0B3FA60");
		UUID registrationUUID = UUID.fromString("AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111");

		getCurrentRegistration(conferenceUUID, registrationUUID, UserInfo.Id.Ryan, UserInfo.AuthCode.Ryan, "");

	}

	private void getCurrentRegistration(UUID conferenceUUID, UUID registrationUUID, UUID userUUID, String authCode, String prevAuthCode)
	{
		ClientResponse<Registration> response = null;

		if(Strings.isNullOrEmpty(prevAuthCode))
			response = conferenceClient.getCurrentRegistration(conferenceUUID, authCode, "");
		else
			response = conferenceClient.getCurrentRegistration(conferenceUUID, authCode, prevAuthCode);

		Assert.assertEquals(response.getStatus(), 200);

		Registration registration = response.getEntity();

		Assert.assertNotNull(registration);
		Assert.assertEquals(registration.getId(), registrationUUID);
		Assert.assertEquals(registration.getUserId(), userUUID);
		Assert.assertEquals(registration.getConferenceId(), conferenceUUID);
		Assert.assertNotNull(registration.getPastPayments());
		Assert.assertFalse(registration.getPastPayments().isEmpty());
	}

	@Test(groups="functional-tests")
	public void getCurrentRegistrationNewAuthCode()
	{
		UUID conferenceUUID = UUID.fromString("1951613E-A253-1AF8-6BC4-C9F1D0B3FA60");
		UUID registrationUUID = UUID.fromString("AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111");

		// check current valid registration for Ryan
		getCurrentRegistration(conferenceUUID, registrationUUID, UserInfo.Id.Ryan, UserInfo.AuthCode.Ryan, "");

		// check transfer of registration from Ryan to Test User
		getCurrentRegistration(conferenceUUID, registrationUUID, UserInfo.Id.TestUser, UserInfo.AuthCode.TestUser, UserInfo.AuthCode.Ryan);

		// restore registration ownership to Ryan
		getCurrentRegistration(conferenceUUID, registrationUUID, UserInfo.Id.Ryan, UserInfo.AuthCode.Ryan, UserInfo.AuthCode.TestUser);
	}

	/**
	 * Test: add a page to conference by fetching the conference and adding a child page to it. this is different than hitting
	 * the createPage endpoint, but should have the same outcome
	 * 
	 * Expected outcome: page should be stored and associated with conference
	 * 
	 * Input: JSON conference resource with new page attached to it
	 * 
	 * Expected return: 204 - No Content
	 */	
	@Test(groups="functional-tests")
	 public void addPageToConferenceByAddingToAConferenceResourceAndUpdating() throws Exception
	 {
		UUID testConferenceId = UUID.randomUUID();
		UUID testPageId = null;
		
		try
		{
			Conference fakeConference = createFakeConference();	
			fakeConference.setId(testConferenceId);

			conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			fakeConference.getRegistrationPages().add(createFakePage());

			createClient();

			ClientResponse<Conference> updateResponse = conferenceClient.updateConference(fakeConference, fakeConference.getId(), UserInfo.AuthCode.TestUser);

			Assert.assertEquals(updateResponse.getStatus(), 204);

			createClient();

			ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
			Conference updatedConference = fetchResponse.getEntity();

			Assert.assertNotNull(updatedConference.getRegistrationPages().get(0));
			testPageId = updatedConference.getRegistrationPages().get(0).getId();
		}
		finally
		{
			deletePageForNextTests(testPageId);
			deleteConferenceForNextTests(testConferenceId);
			sqlConnection.commit();
		}
	 }

	@Test(groups = "functional-tests")
	public void addPageWithBlocksToConferenceByAddingToAConferenceResourceAndUpdating() throws Exception
	{
		UUID testConferenceId = UUID.randomUUID();
		UUID testPageId = null;
		UUID testBlockId = null;

		try
		{
			Conference fakeConference = createFakeConference();
			fakeConference.setId(testConferenceId);

			conferenceClient.createConference(fakeConference, UserInfo.AuthCode.TestUser);

			Page page = createFakePageWithBlocks("Ministry Goals", 1);

			Block block = page.getBlocks().get(0);

			testBlockId = block.getId();

			fakeConference.getRegistrationPages().add(page);

			createClient();

			ClientResponse<Conference> updateResponse = conferenceClient.updateConference(fakeConference, fakeConference.getId(), UserInfo.AuthCode.TestUser);

			Assert.assertEquals(updateResponse.getStatus(), 204);

			createClient();

			ClientResponse<Conference> fetchResponse = conferenceClient.getConference(fakeConference.getId());
			Conference updatedConference = fetchResponse.getEntity();

			Page updatedPage = updatedConference.getRegistrationPages().get(0);

			Assert.assertNotNull(updatedPage);

			testPageId = updatedPage.getId();

			Block updatedBlock = updatedPage.getBlocks().get(0);

			Assert.assertEquals(block.getId(), updatedBlock.getId());

			Assert.assertEquals(block.getProfileType(), updatedBlock.getProfileType());
		}
		finally
		{
			deleteBlockForNextTests(testBlockId);
			deletePageForNextTests(testPageId);
			deleteConferenceForNextTests(testConferenceId);
			sqlConnection.commit();
		}
	}

	@Test(groups="functional-tests")
	public void moveBlockUpOnePage()
	{
		moveBlock(1, 0, 1, UUID.fromString("D5878EBA-9B3F-7F33-8355-3193BF4FB698"));
	}

	@Test(groups="functional-tests")
	public void moveBlockDownOnePage()
	{
		moveBlock(0, 1, 1, UUID.fromString("D5878EBA-9B3F-7F33-8355-3193BF4FB698"));
	}
	
	
	@Test(groups="functional-tests")
	public void grantPermission() throws Exception
	{
		Permission newPermission = createPermission();
		
		try
		{
			ClientResponse response = conferenceClient.grantPermission(ConferenceInfo.Id.NewYork, UserInfo.AuthCode.Ryan, newPermission);
			
			Assert.assertEquals(response.getStatus(), 201);
			
			PermissionEntity retrievedPermission = permissionService.getPermissionBy(newPermission.getId());
			
			Assert.assertNotNull(retrievedPermission);
			Assert.assertEquals(retrievedPermission.getEmailAddress(), UserInfo.Email.Ryan);
			Assert.assertEquals(retrievedPermission.getConferenceId(), ConferenceInfo.Id.NewYork);
			Assert.assertEquals(retrievedPermission.getGivenByUserId(), UserInfo.Id.Ryan);
			Assert.assertEquals(retrievedPermission.getPermissionLevel(), PermissionLevel.UPDATE);
			
			/*permission has not been accepted via email link yet, so this is null*/
			Assert.assertNull(retrievedPermission.getUserId());
			
			/*these two field should hve been set by the system*/
			Assert.assertNotNull(retrievedPermission.getLastUpdatedTimestamp());
			Assert.assertNotNull(retrievedPermission.getActivationCode());
		}
		finally
		{
			sqlConnection.createQuery("DELETE FROM permissions WHERE id = :id")
							.addParameter("id", newPermission.getId())
							.executeUpdate();
			sqlConnection.commit();
		}
	}
	
	@Test(groups="functional-tests")
	public void getRegistrationViews() throws IOException
	{
		ClientResponse<List<RegistrationView>> response = conferenceClient.getRegistrationViews(ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);
		
		List<RegistrationView> registrationViews = response.getEntity();
		
		Assert.assertEquals(registrationViews.size(), 2);
		
		for(RegistrationView view : registrationViews)
		{
			if(view.getId().equals(UUID.fromString("11cfdedf-febc-4011-9b48-44d36bf94997")))
			{
				Assert.assertEquals(view.getName(), "No cats");
				Assert.assertEquals(view.getConferenceId(), ConferenceInfo.Id.NorthernMichigan);
				Assert.assertEquals(view.getCreatedByUserId(), UserInfo.Id.TestUser);
				Assert.assertEquals(view.getVisibleBlockIds(), JsonNodeHelper.toJsonNode("[\"AF60D878-4741-4F21-9D25-231DB86E43EE\",\"DDA45720-DE87-C419-933A-018712B152D2\"]"));
			}
		}
	}
	
	@Test(groups="functional-tests")
	public void createRegistrationView() throws IOException, URISyntaxException
	{
		RegistrationView view = createFakeRegistrationView();
		
		try
		{
			ClientResponse response = conferenceClient.createRegistrationView(ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.TestUser, view);
			
			Assert.assertEquals(response.getStatus(), 201);
			
			RegistrationViewEntity retrievedView = ServiceFactory.createRegistrationViewService(sqlConnection).getRegistrationViewById(view.getId());
			
			Assert.assertNotNull(retrievedView);
			
			Assert.assertEquals(retrievedView.getId(), view.getId());
			Assert.assertEquals(retrievedView.getName(), view.getName());
			Assert.assertEquals(retrievedView.getConferenceId(), view.getConferenceId());
			Assert.assertEquals(retrievedView.getCreatedByUserId(),  view.getCreatedByUserId());
			Assert.assertEquals(retrievedView.getVisibleBlockIds(), view.getVisibleBlockIds());
			
		}
		finally
		{
			 ServiceFactory.createRegistrationViewService(sqlConnection).deleteRegistrationView(view.getId());
			 sqlConnection.commit();
		}
	}
	
	@Test(groups="functional-tests")
	public void getPermissionsForCurrentUserOnConference() throws IOException, URISyntaxException
	{
		ClientResponse<Permission> response = conferenceClient.getPermissionsForCurrentUserOnConference(ConferenceInfo.Id.NorthernMichigan, UserInfo.AuthCode.TestUser);
		
		Assert.assertEquals(response.getStatus(), 200);
		
		Assert.assertEquals(response.getEntity().getPermissionLevel(), PermissionLevel.CREATOR);
	}
	
	private RegistrationView createFakeRegistrationView() throws IOException
	{
		RegistrationView view = new RegistrationView();
		
		view.setId(UUID.randomUUID());
		view.setConferenceId(ConferenceInfo.Id.NorthernMichigan);
		view.setCreatedByUserId(UserInfo.Id.TestUser);
		view.setName("New view");
		view.setVisibleBlockIds(JsonNodeHelper.toJsonNode("[\"AF60D878-4741-4F21-9D25-231DB86E43EE\"]"));
		
		return view;
	}
	private Permission createPermission()
	{
		return new Permission().withRandomID()
								.setConferenceId(ConferenceInfo.Id.NewYork)
								.setGivenByUserId(UserInfo.Id.Ryan)
								.setEmailAddress(UserInfo.Email.Ryan)
								.setPermissionLevel(PermissionLevel.UPDATE);
		
	}
	
	private void moveBlock(int sourcePageIndex, int destinationPageIndex, int blockToRemoveIndex, UUID conferenceUUID)
	{
		// retrieve conference & pages
		Conference webConference = retrieveConferenceProcess.get(conferenceUUID);

		List<Block> sourceBlocks = webConference.getRegistrationPages().get(sourcePageIndex).getBlocks();
		List<Block> destinationBlocks = webConference.getRegistrationPages().get(destinationPageIndex).getBlocks();

		// record original block size
		int sourceBlocksSize = sourceBlocks.size();
		int destinationBlocksSize = destinationBlocks.size();

		// move block from source to destination page
		Block blockToMove = sourceBlocks.remove(blockToRemoveIndex);
		destinationBlocks.add(blockToMove);

		// update conference pages
		ClientResponse updateResponse = conferenceClient.updateConference(webConference, webConference.getId(), UserInfo.AuthCode.Ryan);
		Assert.assertEquals(updateResponse.getStatus(), 204);

		// retrieve conference & pages
		Conference updatedWebConference = retrieveConferenceProcess.get(conferenceUUID);

		List<Block> updatedSourceBlocks = updatedWebConference.getRegistrationPages().get(sourcePageIndex).getBlocks();
		List<Block> updatedDestinationBlocks = updatedWebConference.getRegistrationPages().get(destinationPageIndex).getBlocks();

		// ensure block moved
		Assert.assertEquals(updatedSourceBlocks.size(), sourceBlocksSize-1);
		Assert.assertEquals(updatedDestinationBlocks.size(), destinationBlocksSize+1);
		Assert.assertTrue(updatedDestinationBlocks.contains(blockToMove));
		Assert.assertEquals(updatedDestinationBlocks.get(updatedDestinationBlocks.size()-1), blockToMove);

		// restore block to original page
		updatedDestinationBlocks.remove(blockToMove);
		updatedSourceBlocks.add(blockToMove);

		conferenceClient.updateConference(updatedWebConference, webConference.getId(), UserInfo.AuthCode.Ryan);
	}

	//**********************************************************************
	 // Helper methods
	 //**********************************************************************
	
	private Conference createFakeConference()
	{
		Conference fakeConference = new Conference();
		fakeConference.setName("Fake Fall Retreat");
		fakeConference.setTotalSlots(202);
		fakeConference.setRegistrationStartTime(DateTimeCreaterHelper.createDateTime(2013, 6, 1, 8, 0, 0));
		fakeConference.setRegistrationEndTime(DateTimeCreaterHelper.createDateTime(2013, 6, 22, 23, 59, 59));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 4, 15, 0, 0));
		fakeConference.setEventStartTime(DateTimeCreaterHelper.createDateTime(2013, 7, 9, 11, 0, 0));
		fakeConference.setRegistrationPages(new ArrayList<Page>());
		
		return fakeConference;
	}

	private Page createFakePage()
	{
		Page fakePage = new Page();

		fakePage.setTitle("Ministry Prefs");
		fakePage.setId(UUID.fromString("0a00d62c-af29-3723-f949-95a950a0cccc"));
		fakePage.setPosition(1);
		fakePage.setBlocks(null);

		return fakePage;
	}

	private Page createFakePageWithBlocks(String title, int position)
	{
		Page fakePage = new Page();

		fakePage.setId(UUID.randomUUID());
		fakePage.setTitle(title);
		fakePage.setPosition(position);

		List<Block> blocks = new ArrayList<Block>();
		blocks.add(createFakeBlock(fakePage.getId(), 1, "TextQuestion", jsonNodeFromString("{\"birthDate\":\"November 18, 1990\"}"), ProfileType.BIRTH_DATE));

		fakePage.setBlocks(blocks);

		return fakePage;
	}

	private Block createFakeBlock(UUID pageId, int position, String type, JsonNode content, ProfileType profileType)
	{
		Block block = new Block();

		block.setId(UUID.randomUUID());
		block.setContent(content);
		block.setPageId(pageId);
		block.setType(type);
		block.setProfileType(profileType);
		block.setPosition(position);

		return block;
	}

	private Registration createRegistration(UUID registrationIdUUID, UUID userIdUUID)
	{
		Registration registration = new Registration();

		registration.setId(registrationIdUUID);
		registration.setUserId(userIdUUID);

		return registration;
	}

	private void deleteConferenceForNextTests(UUID conferenceId)
	{
		if(conferenceId != null)
		{
			sqlConnection.createQuery("DELETE FROM permissions WHERE conference_id = :conferenceId")
								.addParameter("conferenceId", conferenceId)
								.executeUpdate();
			
			sqlConnection.createQuery("DELETE FROM conferences WHERE id = :id")
								.addParameter("id", conferenceId)
								.executeUpdate();
		}
	}

	private void deleteAnswerForNextTests(UUID answerId)
	{
		if(answerId != null)
		{
			sqlConnection.createQuery("DELETE FROM answers WHERE id = :id")
					.addParameter("id", answerId)
					.executeUpdate();
		}
	}

	private void deletePageForNextTests(UUID pageId)
	{
		if(pageId != null)
		{
			sqlConnection.createQuery("DELETE FROM pages WHERE id = :id")
					.addParameter("id", pageId)
					.executeUpdate();
		}
	}

	private void deleteBlockForNextTests(UUID blockId)
	{
		if(blockId != null)
		{
			sqlConnection.createQuery("DELETE FROM blocks WHERE id = :id")
					.addParameter("id", blockId)
					.executeUpdate();
		}
	}

	private void deleteRegistrationForNextTests(UUID registrationId)
	{
		if(registrationId != null)
		{
			sqlConnection.createQuery("DELETE FROM registrations WHERE id = :id")
								.addParameter("id", registrationId)
								.executeUpdate();
		}
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