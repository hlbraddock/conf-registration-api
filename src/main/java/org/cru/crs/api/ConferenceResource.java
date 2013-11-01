package org.cru.crs.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ccci.util.time.Clock;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.crs.api.model.Answer;
import org.cru.crs.api.model.Conference;
import org.cru.crs.api.model.Page;
import org.cru.crs.api.model.Payment;
import org.cru.crs.api.model.Registration;
import org.cru.crs.api.model.utils.ConferenceAssembler;
import org.cru.crs.api.utils.RegistrationWindowCalculator;
import org.cru.crs.auth.CrsUserService;
import org.cru.crs.auth.UnauthorizedException;
import org.cru.crs.auth.model.CrsApplicationUser;
import org.cru.crs.model.AnswerEntity;
import org.cru.crs.model.ConferenceEntity;
import org.cru.crs.model.PageEntity;
import org.cru.crs.model.PaymentEntity;
import org.cru.crs.model.RegistrationEntity;
import org.cru.crs.service.AnswerService;
import org.cru.crs.service.BlockService;
import org.cru.crs.service.ConferenceCostsService;
import org.cru.crs.service.ConferenceService;
import org.cru.crs.service.PageService;
import org.cru.crs.service.PaymentService;
import org.cru.crs.service.RegistrationService;
import org.cru.crs.utils.IdComparer;
import org.jboss.logging.Logger;
import org.testng.collections.Lists;

import com.beust.jcommander.internal.Sets;

@Stateless
@Path("/conferences")
public class ConferenceResource
{
	@Inject ConferenceService conferenceService;
	@Inject ConferenceCostsService conferenceCostsService;
	@Inject RegistrationService registrationService;
	@Inject PageService pageService;
	@Inject BlockService blockService;
	@Inject PaymentService paymentService;
	@Inject AnswerService answerService;
	@Inject Clock clock;

	@Inject CrsUserService userService;

	Logger logger = Logger.getLogger(ConferenceResource.class);

    /**
	 * Desired design: Gets all the conferences for which the authenticated user has access to.
	 * 
	 * Current status: Returns all conferences in the system.  Need authentication built out to
	 * implement as designed.
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConferences(@HeaderParam(value="Authorization") String authCode)
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);

            List<Conference> conferences = Lists.newArrayList();
            
            for(ConferenceEntity databaseConference : conferenceService.fetchAllConferences(loggedInUser))
            {
            	Conference webConference = ConferenceAssembler.buildConference(databaseConference.getId(), conferenceService, conferenceCostsService, pageService, blockService);
            													            	
            	RegistrationWindowCalculator.setRegistrationOpenFieldOn(webConference, clock);
                RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(webConference, clock);
                
                conferences.add(webConference);
            }

			return Response.ok(conferences).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	/**
	 * Return the conference identified by ID with all associated Pages and Blocks
	 * 
	 * @param conferenceId
	 * @return
	 */
	@GET
	@Path("/{conferenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConference(@PathParam(value = "conferenceId") UUID conferenceId)
	{
		
		Conference requestedConference = ConferenceAssembler.buildConference(conferenceId, conferenceService, conferenceCostsService, pageService, blockService);
		
		if(requestedConference == null) return Response.status(Status.NOT_FOUND).build();
        
        /*
         * Set these fields based on the server's time, not the client's.  The client could be in
         * any timezone..
         */
        RegistrationWindowCalculator.setRegistrationOpenFieldOn(requestedConference, clock);
        RegistrationWindowCalculator.setEarlyRegistrationOpenFieldOn(requestedConference, clock);

        logger.info("GET: " + requestedConference.getId());
        logObject(requestedConference, logger);

        return Response.ok(requestedConference).build();
	}

	/**
	 * Creates a new conference.  In order to create a conference the user must be logged in
	 * with a known identity.  To check this, we look in the session for an appUserId.
	 * 
	 * @param conference
	 * @return
	 * @throws URISyntaxException
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createConference(Conference conference, 
			@HeaderParam(value="Authorization") String authCode)throws URISyntaxException
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);

			/*if there is no id in the conference, then create one. the client has the ability, but not 
			 * the obligation to create one*/
			if(conference.getId() == null)
			{
				conference.setId(UUID.randomUUID());
			}

			/*persist the new conference*/
			conferenceService.createNewConference(conference.toJpaConferenceEntity(), loggedInUser);
			
			/*fetch the created conference so a nice pretty conference object can be returned to client*/
			Conference createdConference = ConferenceAssembler.buildConference(conference.getId(), conferenceService, conferenceCostsService, pageService, blockService);
			
			/*return a response with status 201 - Created and a location header to fetch the conference.
			 * a copy of the entity is also returned.*/
			return Response.status(Status.CREATED)
							.location(new URI("/conferences/" + conference.getId()))
							.entity(createdConference).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	/**
	 * Updates an existing conference
	 * 
	 * This PUT will create a new conference resource if the conference specified by @Param conferenceId does
	 * not exist.
	 * 
	 * If the conference ID path parameter and the conference ID body parameter (in the JSON object) do not match
	 * then this method will fail-fast and return a 400-Bad Request response.
	 * 
	 * @param conference
	 * @return
	 */
	@PUT
	@Path("/{conferenceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateConference(Conference conference, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value="Authorization") String authCode)
	{

		try
		{
			CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);

			/*Check if the conference IDs are both present, and are different.  If so then throw a 400 - Bad Request*/
			if(IdComparer.idsAreNotNullAndDifferent(conferenceId, conference.getId()))
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			if(conferenceId == null)
			{
				/* If a conference ID wasn't passed in, then create a new conference.*/
				conferenceService.createNewConference(conference.toJpaConferenceEntity().setId(UUID.randomUUID()), 
						loggedInUser);
			}
			else if(conferenceService.fetchConferenceBy(conferenceId) == null)
			{
				/* If the conference id was passed in, but there's no conference associated with it, then create a new conference*/ 
				conferenceService.createNewConference(conference.toJpaConferenceEntity().setId(conferenceId), 
						loggedInUser);
			}
			else
			{
				logger.info("PUT: " + conference.getId());
                logObject(conference, logger);

				/*there is an existing conference, so go update it*/
				conferenceService.updateConference(conference.toJpaConferenceEntity().setId(conferenceId), 
						loggedInUser);
			}
			
			return Response.noContent().build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/{conferenceId}/pages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPage(Page newPage, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);
			final ConferenceEntity conferencePageBelongsTo = conferenceService.fetchConferenceBy(conferenceId);

			/*if there is no conference identified by the passed in id, then return a 400 - bad request*/
			if(conferencePageBelongsTo == null)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}

			if(!conferencePageBelongsTo.getContactPersonId().equals(loggedInUser.getId()))
			{
				throw new UnauthorizedException();
			}
			
			if(newPage.getId() == null)
			{
				newPage.setId(UUID.randomUUID());
			}
			
			newPage.setConferenceId(conferenceId);
			
			pageService.savePage(newPage.toDbPageEntity());
			
			PageEntity createdPage = pageService.fetchPageBy(newPage.getId());
			
			return Response.status(Status.CREATED)
					.location(new URI("/pages/" + newPage.getId()))
					.entity(Page.fromDb(createdPage, blockService.fetchBlocksForPage(createdPage.getId())))
					.build();
		} 
		catch (UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/{conferenceId}/registrations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRegistration(Registration newRegistration, 
			@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			logger.info(crsLoggedInUser);

			if(newRegistration.getId() == null) newRegistration.setId(UUID.randomUUID());

            logger.info(conferenceId);

            ConferenceEntity conference = conferenceService.fetchConferenceBy(conferenceId);
            if(conference == null) return Response.status(Status.BAD_REQUEST).build();

            RegistrationEntity newRegistrationEntity = newRegistration.toDbRegistrationEntity();

            logObject(newRegistrationEntity, logger);

            newRegistrationEntity.setUserId(crsLoggedInUser.getId());
            newRegistrationEntity.setConferenceId(conferenceId);
            
            registrationService.createNewRegistration(newRegistrationEntity, crsLoggedInUser);

            if(conference.getConferenceCostsId() != null)
            {			
            	if(conferenceCostsService.fetchBy(conference.getConferenceCostsId()).isAcceptCreditCards())
            	{
            		PaymentEntity newPayment = new PaymentEntity().setId(UUID.randomUUID()).setRegistrationId(newRegistration.getId());
            		paymentService.createPaymentRecord(newPayment, crsLoggedInUser);
            	}
            }
            
			return Response.status(Status.CREATED)
								.location(new URI("/pages/" + newRegistration.getId()))
								.entity(newRegistration)
								.build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@GET
	@Path("/{conferenceId}/registrations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistrations(@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			logger.info(conferenceId);

			CrsApplicationUser crsLoggedInUser = userService.getLoggedInUser(authCode);

			logger.info(crsLoggedInUser);

			Set<Registration> registrationSet = Registration.fromDb(registrationService.fetchAllRegistrations(conferenceId, crsLoggedInUser));

			for(Registration registration : registrationSet)
			{
				addPaymentsToRegistration(registration);
				addAnswersToRegistration(registration);
			}
			
			logObject(registrationSet, logger);
			
			return Response.ok(registrationSet).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}
	
	@GET
	@Path("/{conferenceId}/registrations/current")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentRegistration(@PathParam(value = "conferenceId") UUID conferenceId,
			@HeaderParam(value = "Authorization") String authCode) throws URISyntaxException
	{
		try
		{
			logger.info(conferenceId);

			CrsApplicationUser loggedInUser = userService.getLoggedInUser(authCode);

			logger.info(loggedInUser);

			RegistrationEntity registrationEntity = registrationService.getRegistrationByConferenceIdUserId(conferenceId, loggedInUser.getId(), loggedInUser);

			if(registrationEntity == null) return Response.status(Status.NOT_FOUND).build();
			
			Registration registration = Registration.fromDb(registrationEntity);
			
			addAnswersToRegistration(registration);
			addPaymentsToRegistration(registration);
			
			logObject(registration, logger);

			return Response.ok(registration).build();
		}
		catch(UnauthorizedException e)
		{
			return Response.status(Status.UNAUTHORIZED).build(); 
		}
	}

	private void addPaymentsToRegistration(Registration registration)
	{
		List<PaymentEntity> paymentEntitiesForRegistration = paymentService.fetchPaymentsForRegistration(registration.getId());
		List<Payment> pastPayments = Lists.newArrayList();
		
		for(PaymentEntity paymentEntity : paymentEntitiesForRegistration)
		{
			Payment payment = Payment.fromJpa(paymentEntity);
			if(payment.getAuthnetTransactionId() != null) pastPayments.add(payment);
			else registration.setCurrentPayment(payment);
		}
		registration.setPastPayments(pastPayments);
	}

	private void addAnswersToRegistration(Registration registration)
	{
		List<AnswerEntity> answerEntitiesForRegistration = answerService.getAllAnswersForRegistration(registration.getId());
		Set<Answer> answers = Sets.newHashSet();
		
		for(AnswerEntity answerEntity : answerEntitiesForRegistration)
		{
			answers.add(Answer.fromJpa(answerEntity));
		}
		registration.setAnswers(answers);
	}



	private void logObject(Object object, Logger logger)
	{
		try
		{
			logger.info(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(object));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
